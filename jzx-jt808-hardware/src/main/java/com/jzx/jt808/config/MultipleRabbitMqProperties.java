package com.jzx.jt808.config;

import lombok.Getter;
import lombok.Setter;

/**
 * rabbit配置类
 * 
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
@Getter
@Setter
public class MultipleRabbitMqProperties {
    private String host;
    private String password;
    private int port;
    private String username;
    private String virtualHost;
}
