package com.ren.codesandbox.specific;

import com.ren.codesandbox.model.ExecMessage;
import com.ren.codesandbox.model.ExecuteCodeRequest;
import com.ren.codesandbox.model.ExecuteCodeResponse;
import com.ren.codesandbox.template.JavaCodeSandBoxTemplate;
import com.ren.codesandbox.utils.ProcessUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JavaNativeCodeSandBox extends JavaCodeSandBoxTemplate {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}
