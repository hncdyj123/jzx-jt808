package com.jzx.jt808.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jzx.jt808.enums.TerminalOrderEnums;

/**
 * 类描述：协议类型注解类
 *
 * @author yangjie
 * @date 2023-07-06 09:32
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TerminalOrderType {
    /**
     * 协议枚举
     * 
     * @return
     */
    TerminalOrderEnums value();

    /**
     * 协议指令
     * 
     * @return
     */
    int order() default 0;

    /**
     * 协议指令描述
     *
     * @return
     */
    String orderDesc() default "";

    /**
     * 是否初始化
     * 
     * @return
     */
    boolean initialize() default false;
}
