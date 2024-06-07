package com.jzx.jt808.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回消息实体类
 * 
 * @author 杨杰
 * @version 2022-2-25
 * @see Message
 * @since
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    /**
     * 返回码
     */
    private Integer code = 200;
    /**
     * 返回消息
     */
    private String message = "success";
    /**
     * 返回消息体
     */
    private Object result;
}
