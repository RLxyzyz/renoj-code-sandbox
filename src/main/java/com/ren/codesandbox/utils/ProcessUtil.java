package com.ren.codesandbox.utils;

import com.ren.codesandbox.model.ExecMessage;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author 任磊
 * @version 1.0
 * @project renoj-code-sandbox
 * @description 进程工具类
 * @date 2024/2/1 17:31:35
 */
public class ProcessUtil {
    public static ExecMessage runProcessAndGetMessage(Process process,String optionName)
    {
        ExecMessage message=new ExecMessage();
        //等待程序执行，获取错误码
        int execValue= 0;
        try {
            StopWatch  stopWatch=new StopWatch();
            stopWatch.start();
            execValue = process.waitFor();
            message.setExecValue(execValue);
            //正常退出
            if (execValue==0)
            {
                System.out.println(optionName+" success");
                StringBuilder compileOutputBuilder=new StringBuilder();
                BufferedReader reader=new BufferedReader(new InputStreamReader(process.getInputStream()));
                String compileOutputLine;
                while ((compileOutputLine=reader.readLine())!=null)
                {
                    compileOutputBuilder.append(compileOutputLine).append('\n');
                    System.out.println(compileOutputLine);
                }
                message.setMessage(compileOutputBuilder.toString());
            }else {
                //异常退出
                System.out.println("error,code:"+execValue);
                BufferedReader reader=new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String compileErrorOutputLine;
                StringBuilder compileErrorBulider=new StringBuilder();
                while ((compileErrorOutputLine=reader.readLine())!=null)
                {
                    compileErrorBulider.append(compileErrorOutputLine).append('\n');
                    System.out.println(compileErrorOutputLine );
                }
                message.setErrorMessage(compileErrorBulider.toString());
            }
            stopWatch.stop();
            long lastTaskTimeMillis = stopWatch.getLastTaskTimeMillis();
            message.setTime(lastTaskTimeMillis);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }
}
