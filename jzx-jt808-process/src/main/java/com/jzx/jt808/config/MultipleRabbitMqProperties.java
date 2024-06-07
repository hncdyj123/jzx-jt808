package com.jzx.jt808.config;

import lombok.Getter;
import lombok.Setter;

/**
 * 类描述：rabbit配置类
 *
 * @author yangjie
 * @date 2022-07-22 16:42
 **/
@Getter
@Setter
public class MultipleRabbitMqProperties {
    private String host;
    private String password;
    private int port;
    private String username;
    private String virtualHost;
}
