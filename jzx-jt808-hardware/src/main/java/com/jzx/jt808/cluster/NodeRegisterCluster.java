package com.jzx.jt808.cluster;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.jzx.jt808.BusinessConstants;
import com.jzx.jt808.session.SessionChannelManager;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 节点注册管理配置类
 * 
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
@Slf4j
@Component
public class NodeRegisterCluster {
    /**
     * 集群用redis,从redis
     */
    @Resource
    private RedisTemplate<String, Object> slaveRedisTemplate;
    @Resource
    private SessionChannelManager sessionChannelManager;
    @Value("${spring.profiles.active:''}")
    private String env;
    @Value("${business.netty.port}")
    private String port;
    @Value("${business.localOuterIp}")
    private String localOuterIp;
    @Value("${business.hostName}")
    private String hostName;
    /**
     * 队列名称
     **/
    public static String REDIS_QUEUE = "";
    /**
     * 是否启动正式环境
     */
    private static final String ENV_PRD = "prd";
    /**
     * 节点超时时间180秒
     **/
    private static int NODE_TIME_OUT = 3 * 60;
    /**
     * 注册线程休眠时间
     */
    private static int NODE_SLEEP_TIME = 2 * 60 * 1000;

    @PostConstruct
    public void initMethod() {
        new Thread(() -> {
            try {
                while (true) {
                    if (StrUtil.isNotEmpty(env)) {
                        if (StrUtil.isBlank(localOuterIp)) {
                            log.error("NodeRegisterConfig 当前启动环境为:{},没有配置外网ip,无法集群.", env);
                            return;
                        }
                        log.warn("NodeRegisterConfig 当前机器外网地址为:{}", localOuterIp);
                        slaveRedisTemplate.opsForValue().set(BusinessConstants.DOMAIN_ADDRESS + localOuterIp,
                            localOuterIp + "|" + port + "|" + sessionChannelManager.getAuthUserCount());
                        slaveRedisTemplate.expire(BusinessConstants.DOMAIN_ADDRESS + localOuterIp, NODE_TIME_OUT,
                            TimeUnit.SECONDS);
                        REDIS_QUEUE = hostName;
                        if (log.isDebugEnabled()) {
                            log.debug("当前服务>host名称为:{}", REDIS_QUEUE);
                        }
                    }
                    Thread.sleep(NODE_SLEEP_TIME);
                }
            } catch (Exception e) {
                log.error("NodeRegisterConfig error:", e);
            }
        }).start();
    }
}
