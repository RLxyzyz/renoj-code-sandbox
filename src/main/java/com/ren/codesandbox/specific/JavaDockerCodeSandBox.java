package com.ren.codesandbox.specific;

import cn.hutool.core.util.ArrayUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.ren.codesandbox.model.ExecMessage;
import com.ren.codesandbox.model.ExecuteCodeRequest;
import com.ren.codesandbox.template.JavaCodeSandBoxTemplate;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JavaDockerCodeSandBox extends JavaCodeSandBoxTemplate {
    @Override
    public List<ExecMessage> runCode(File userCodeFile, List<String> inputList) {
        String userCodeParentPath=userCodeFile.getParentFile().getAbsolutePath();
        //3拉取镜像
        DockerClient dockerClient = DockerClientBuilder.getInstance("tcp://192.168.110.128:2375").build();
        /*PullImageResultCallback pullImageResultCallback=new PullImageResultCallback(){
            @Override
            public void onNext(PullResponseItem item) {
                System.out.println(item.getStatus());
                super.onNext(item);
            }
        };
        try {
            dockerClient.pullImageCmd(IMAGE).exec(pullImageResultCallback).awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("download success");*/
        //创建容器
        HostConfig hostConfig=new HostConfig();
        hostConfig.setBinds(new Bind(userCodeParentPath,new Volume("/app")));
        hostConfig.withMemory(100*1000*1000L);
        hostConfig.withCpuCount(1L);
        hostConfig.withMemorySwap(0L);
        CreateContainerResponse exec = dockerClient.createContainerCmd(IMAGE)
                .withHostConfig(hostConfig)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withTty(true)
                .withNetworkDisabled(true)
                .withReadonlyRootfs(true)
                .exec();
        String containerId = exec.getId();
        //启动容器
        dockerClient.startContainerCmd(containerId).exec();
        System.out.println("start success");
        System.out.println("create success");

        List<ExecMessage> execMessageList=new ArrayList<>();

        //执行程序
        for (String inputArgs : inputList) {
            StopWatch stopWatch=new StopWatch();
            String[] inputArgsArray = inputArgs.split(" ");
            String[] cmdArrays= ArrayUtil.append(new String[]{"java","-cp","/app","Main"},inputArgsArray);
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArrays)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .exec();
            String execCreateCmdResponseId = execCreateCmdResponse.getId();
            System.out.println("exec mingling"+execCreateCmdResponse);
            ExecMessage execMessage=new ExecMessage();
            final String[] message = {null};
            final String[] errorMessage = {null};
            final boolean[] timeout = {true};
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback(){
                @Override
                public void onComplete() {
                    timeout[0] =false;
                    super.onComplete();
                }

                @Override
                public void onNext(Frame frame) {

                    StreamType streamType = frame.getStreamType();
                    if (streamType.equals(StreamType.STDERR))
                    {
                        errorMessage[0] =new String(frame.getPayload());
                        System.out.println(errorMessage[0]);
                    }else {
                        message[0] =new String(frame.getPayload());
                        System.out.println(message[0]);
                    }
                    super.onNext(frame);
                }
            };
            final long[] maxMemory = {0L};
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            statsCmd.exec(new ResultCallback<Statistics>() {
                @Override
                public void onStart(Closeable closeable) {

                }

                @Override
                public void onNext(Statistics statistics) {
                    maxMemory[0] =Math.max(maxMemory[0],statistics.getMemoryStats().getUsage());
                    System.out.println(maxMemory[0]);
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }

                @Override
                public void close() throws IOException {

                }
            });
            try {
                stopWatch.start();
                dockerClient.execStartCmd(execCreateCmdResponseId).exec(execStartResultCallback).awaitCompletion(OUT_TIME, TimeUnit.MILLISECONDS);
                stopWatch.stop();
                statsCmd.close();
                long lastTaskTimeMillis = stopWatch.getLastTaskTimeMillis();
                execMessage.setTime(lastTaskTimeMillis);
                execMessage.setMessage(message[0]);
                //判断有没有超时
                if (!timeout[0])
                {
                    execMessage.setExecValue(1);
                }
                execMessage.setErrorMessage(errorMessage[0]);
                execMessage.setMemory(maxMemory[0]);
                execMessageList.add(execMessage);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return execMessageList;
    }
}
