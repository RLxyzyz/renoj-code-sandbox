package com.ren.codesandbox;

import com.ren.codesandbox.model.ExecMessage;
import com.ren.codesandbox.model.ExecuteCodeRequest;
import com.ren.codesandbox.model.ExecuteCodeResponse;

import java.util.List;

/**
 * @author 任磊
 * @version 1.0
 * @project renoj-backend
 * @description 代码沙箱接口，只需要实现该接口就可以实现代码沙箱功能,提高通用性，
 * @date 2023/12/12 21:26:14
 */
public interface CodeSandbox {
    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
