package com.jzx.jt808.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * redis连接配置类
 * 
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MultipleRedisProperties {
    /**
     * 连接地址
     */
    private String host;
    /**
     * 连接端口
     */
    private Integer port;
    /**
     * 连接密码
     */
    private String password;
    /**
     * 连接超时时间
     */
    private Long timeout;
    /**
     * 使用redis数据库编号(0-15)
     */
    private Integer database;
    /**
     * 连接池最大连接数(使用负值表示没有限制)
     */
    private Integer poolMaxActive;
    /**
     * 连接池中的最大空闲连接
     */
    private Integer poolMaxIdle;
    /**
     * 连接池最大阻塞等待时间(使用负值表示没有限制)
     */
    private Integer poolMaxWait;
    /**
     * 连接池中的最小空闲连接
     */
    private Integer poolMinIdle;
    /**
     * 动态创建redisTemplate注册Bean名称
     */
    private String redisBeanName;
}
