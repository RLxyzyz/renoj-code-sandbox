package com.ren.codesandbox.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 任磊
 * @version 1.0
 * @project renoj-code-sandbox
 * @description 执行结果对象
 * @date 2024/2/1 17:33:53
 */
@Getter
@Setter
public class ExecMessage {
    //执行结果
    private Integer execValue;
    //执行结果信息
    private String message;
    //错误执行的信息
    private String errorMessage;
    //程序执行时间
    private Long time;
    //程序执行内存
    private Long memory;
}
