package com.jzx.jt808;

/**
 * 系统常量类
 * 
 * @author yangjie
 * @date 2023/8/25
 * @version 1.0.0
 */
public class BusinessConstants {
    /**
     * 所有设备sessionKey头
     */
    public static final String JZX_SESSION_KEY = "jzx:session:";
    /**
     * 当前部署服务的外网IP
     */
    public static final String LOCAL_OUTER_IP = "local_outer_ip";
    /**
     * 集群用到redisKey头
     */
    public static final String DOMAIN_ADDRESS = "jt808:domain:address:";
    /** 鉴权业务交换机名称 **/
    public static final String JT808_AUTH_EXCHANGE = "jt808.auth.exchange";
    /** 鉴权业务队列名称 **/
    public static final String JT808_AUTH_QUEUE = "jt808.auth.queue";
    /** 鉴权业务路由key **/
    public static final String JT808_AUTH_ROUTE_KEY = "jt808.auth.routeKey";

    /** 登录业务交换机名称 **/
    public static final String JT808_LOGIN_EXCHANGE = "jt808.login.exchange";
    /** 登录业务队列名称 **/
    public static final String JT808_LOGIN_QUEUE = "jt808.login.queue";
    /** 登录业务路由key **/
    public static final String JT808_LOGIN_ROUTE_KEY = "jt808.login.routeKey";

    /** 心跳指令交换机名称 **/
    public static final String JT808_HEART_EXCHANGE = "jt808.heart.exchange";
    /** 心跳指令队列名称 **/
    public static final String JT808_HEART_QUEUE = "jt808.heart.queue";
    /** 心跳指令路由key **/
    public static final String JT808_HEART_ROUTE_KEY = "jt808.heart.routeKey";

    /** 位置上报交换机名称 **/
    public static final String JT808_LOCATION_EXCHANGE = "jt808.location.exchange";
    /** 位置上报队列名称 **/
    public static final String JT808_LOCATION_QUEUE = "jt808.location.queue";
    /** 位置上报路由key **/
    public static final String JT808_LOCATION_ROUTE_KEY = "jt808.location.routeKey";

    public static final String SYSTEM_LOG_EXCHANGE = "system.log.exchange";
    public static final String SYSTEM_LOG_QUEUE = "system.log.queue";
    public static final String SYSTEM_LOG_ROUTE_KEY = "system.log.routeKey";
    public final static String PROTOCOL_KEY = "protocol";
    public final static String PROTOCOL_JTT808 = "jt808";
    /** 指令下发key **/
    public final static String SYSTEM_COMMAND_DOWN = "system:command:down";
    /**
     * 协议标识
     */
    public final static int PACKAGE_HEADER_JT808 = 0x7e;

    public final static String COMMAND_COLLECTION_NAME = "device_command";

    public final static String LOG_COLLECTION_NAME = "device_logs_";
}
