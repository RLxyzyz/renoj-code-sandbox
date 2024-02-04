package com.ren.codesandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PingCmd;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DockerClientBuilder;

/**
 * @author 任磊
 * @version 1.0
 * @project renoj-code-sandbox
 * @description
 * @date 2024/2/2 19:31:10
 */
public class DockerDemo {
    public static void main(String[] args) throws InterruptedException {
        DockerClient build = DockerClientBuilder.getInstance("tcp://192.168.110.128:2375").build();
        Info exec = build.infoCmd().exec();
        System.out.println(exec.getImages().toString());
    }
}
