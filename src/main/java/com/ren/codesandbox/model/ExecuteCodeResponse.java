package com.ren.codesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 任磊
 * @version 1.0
 * @project renoj-backend
 * @description 代码沙箱响应数据
 * @date 2023/12/12 21:31:22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteCodeResponse {
    private List<String> outputList;
    /**
     * 程序执行(接口)的信息
     * */
    private String message;
    /**
     * 程序执行的状态
     * */
    private Integer status;
    /**
     * 判题信息
     * */
    private JudgeInfo judgeInfo;
}
