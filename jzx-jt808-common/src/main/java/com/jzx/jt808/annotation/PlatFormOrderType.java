package com.jzx.jt808.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jzx.jt808.enums.PlatformOrderEnums;

/**
 * 类描述：协议类型注解类
 * 
 * @author yangjie
 * @date 2023/8/25
 * @version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface PlatFormOrderType {
    /**
     * 协议枚举
     * 
     * @author yangjie
     * @date 2023/8/25
     * @param
     * @return {@link PlatformOrderEnums}
     */
    PlatformOrderEnums value();

    /**
     * 协议指令描述
     * 
     * @author yangjie
     * @date 2023/8/25
     * @param
     * @return {@link String}
     */
    String orderDesc() default "";

    /**
     * 是否初始化
     * 
     * @author yangjie
     * @date 2023/8/25
     * @param
     * @return {@link boolean}
     */
    boolean initialize() default false;
}
