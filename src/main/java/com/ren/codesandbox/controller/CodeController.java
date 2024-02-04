package com.ren.codesandbox.controller;

import com.ren.codesandbox.CodeSandbox;
import com.ren.codesandbox.model.ExecuteCodeRequest;
import com.ren.codesandbox.model.ExecuteCodeResponse;
import com.ren.codesandbox.specific.JavaNativeCodeSandBox;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 任磊
 * @version 1.0
 * @project renoj-code-sandbox
 * @description 执行代码沙箱的接口
 * @date 2024/2/4 21:10:26
 */
@RestController("/")
public class CodeController {

    @Resource
    private JavaNativeCodeSandBox codeSandBox;

    @PostMapping("/executeCode")
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest)
    {
        if (executeCodeRequest==null)
        {
            throw new RuntimeException("请求参数为空");
        }
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        return executeCodeResponse;
    }
}
