package com.ren.codesandbox.model;

import lombok.Data;

/**
 * @author 任磊
 * @version 1.0
 * @project renoj-backend
 * @description 判题信息
 * @date 2023/11/28 21:17:07
 */
@Data
public class JudgeInfo {
    /*
    * 程序执行时间(ms)
    * */
    private Long time;
    /*
    * 程序信息
    * */
    private String message;
    /*
    * 消耗内存
    * */
    private Long memory;
}
