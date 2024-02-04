package com.ren.codesandbox.template;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import com.ren.codesandbox.CodeSandbox;
import com.ren.codesandbox.model.ExecMessage;
import com.ren.codesandbox.model.ExecuteCodeRequest;
import com.ren.codesandbox.model.ExecuteCodeResponse;
import com.ren.codesandbox.model.JudgeInfo;
import com.ren.codesandbox.specific.JavaDockerCodeSandBox;
import com.ren.codesandbox.specific.JavaNativeCodeSandBox;
import com.ren.codesandbox.utils.ProcessUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
@Slf4j
public abstract class JavaCodeSandBoxTemplate implements CodeSandbox {
    protected final String GLOBAL_CODE_PATH_NAME="tmpCode";
    protected final String USER_CODE_PATH_NAME="Main.java";
    protected final String IMAGE="openjdk:19-jdk";
    protected final long OUT_TIME=5000L;
    protected String SECURITY_MANAGER_PATH="F:\\code\\backend\\renoj-code-sandbox\\src\\main\\resources\\security";
    public File createAndSaveFile(String code)
    {
        //首先判断目录是否存在
        String userDir = System.getProperty("user.dir");
        String globalCodePathName=userDir+ File.separator+GLOBAL_CODE_PATH_NAME;
        if (!FileUtil.exist(globalCodePathName))
        {
            FileUtil.mkdir(globalCodePathName);
        }
        //把用户的代码隔离存放
        UUID uuid = UUID.randomUUID();
        String userCodeParentPath = globalCodePathName+File.separator+ uuid;
        String path="/home/renlei/renoj-code-sandbox/tmpCode"+File.separator+uuid;
        File userCodeFile = FileUtil.writeString(code, userCodeParentPath + File.separator + USER_CODE_PATH_NAME, StandardCharsets.UTF_8);
        return userCodeFile;
    }
    public ExecMessage compile(File userCodeFile)
    {
        //编写命令，编译程序
        String compileCmd=String.format("javac %s",userCodeFile.getAbsolutePath());
        System.out.println(userCodeFile.getAbsolutePath());
        try {
            Process compileProcess=Runtime.getRuntime().exec(compileCmd);
            ExecMessage message = ProcessUtil.runProcessAndGetMessage(compileProcess,"compile");
            return message;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public List<ExecMessage> runCode(File userCodeFile,List<String> inputList)
    {
        String userDir = System.getProperty("user.dir");
        String globalCodePathName=userDir+ File.separator+GLOBAL_CODE_PATH_NAME;
        String userCodeParentPath=userCodeFile.getParentFile().getAbsolutePath();
        List<ExecMessage> execMessageList=new ArrayList<>();
        for (String input : inputList) {
            String runCmd=String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s;%s -Djava.security.manager=MySecurityManager Main %s",userCodeParentPath,SECURITY_MANAGER_PATH,input);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                //守护线程，当程序运行时间超过最大值，将运行线程停止
                new Thread(()->{
                    try {
                        Thread.sleep(OUT_TIME);
                        boolean alive = runProcess.isAlive();
                        if (alive)
                        {
                            runProcess.destroy();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }).start();
                ExecMessage message = ProcessUtil.runProcessAndGetMessage(runProcess,"run");
                execMessageList.add(message);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return execMessageList;
    }
    public ExecuteCodeResponse getOutputResponse(List<ExecMessage> execMessageList)
    {
        //封装返回结果
        ExecuteCodeResponse response=new ExecuteCodeResponse();
        List<String> outputList=new ArrayList<>();
        long maxTime=0;
        long maxMemory=0L;
        boolean flag=false;
        for (ExecMessage message : execMessageList) {
            if (message.getExecValue()==1)
            {
                flag=true;
            }
            if (StrUtil.isNotBlank(message.getErrorMessage()))
            {
                response.setMessage(message.getErrorMessage());
                response.setStatus(3);
                break;
            }
            if (message.getTime()>maxTime&&message.getTime()!=null)
            {
                maxTime=message.getTime();
            }
            if (message.getMemory()!=null)
            {
                maxMemory=Math.max(maxMemory,message.getMemory());
            }
            outputList.add(message.getMessage());
        }
        System.out.println(maxTime);
        //没有r出现错误
        if (outputList.size()==execMessageList.size()&&!flag)
        {
            response.setStatus(1);
            response.setOutputList(outputList);
        }
        JudgeInfo judgeInfo=new JudgeInfo();
        judgeInfo.setTime(maxTime);
        judgeInfo.setMemory(maxMemory);
        response.setJudgeInfo(judgeInfo);
        return response;
    }
    public boolean clearFile(File userCodeFile)
    {
        String userCodeParentPath =userCodeFile.getParentFile().getAbsolutePath();
        //清理文件
        if (userCodeFile.getParentFile()!=null)
        {
            boolean del = FileUtil.del(userCodeParentPath);
            return del;
        }
        return true;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        //获取执行代码以及输出
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        List<String> inputList = executeCodeRequest.getInputList();
        //创建文件以及保存代码
        File userCodeFile = createAndSaveFile(code);
        //编译代码
        ExecMessage execMessage = compile(userCodeFile);
        //判断编译有没有出错
        if (StrUtil.isNotBlank(execMessage.getErrorMessage()))
        {
            return getErrorResponse(new RuntimeException());
        }
        //执行代码
        List<ExecMessage> execMessageList = runCode(userCodeFile, inputList);
        //封装执行结果
        ExecuteCodeResponse outputResponse = getOutputResponse(execMessageList);
        //清理文件
        boolean b = clearFile(userCodeFile);
        if (!b)
        {
            log.info("delete file error,userCodeFilePath = {}",userCodeFile.getAbsolutePath());
        }

        return outputResponse;
    }

    protected ExecuteCodeResponse getErrorResponse(Throwable e)
    {
        ExecuteCodeResponse executeCodeResponse=new ExecuteCodeResponse();
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        executeCodeResponse.setMessage(e.getMessage());
        executeCodeResponse.setOutputList(new ArrayList<>());
        executeCodeResponse.setStatus(2);
        return executeCodeResponse;
    }
}
