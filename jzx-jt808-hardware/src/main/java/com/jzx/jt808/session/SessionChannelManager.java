package com.jzx.jt808.session;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.jzx.jt808.BusinessConstants;
import com.jzx.jt808.cluster.NodeRegisterCluster;
import com.jzx.jt808.entity.ComLogDto;
import com.jzx.jt808.session.vo.Session;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * Session管理类<br>
 * 1、提供channelId到session映射关系管理<br>
 * 2、提供用户标识到channelId管理<br>
 *
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
@Slf4j
@Component
public class SessionChannelManager {
    /**
     * 维护本地接入的设备session<br/>
     * 使用主redis<br>
     */
    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    @Qualifier("logsRabbitTemplate")
    private RabbitTemplate rabbitTemplate;
    @Value("${business.netty.port}")
    private Integer port;

    @Value("${business.sessionKeepTime}")
    private Integer sessionKeepTime;
    /**
     * channelId和Session映射关系Map
     */
    private ConcurrentMap<String, Session> sessionIdMap = new ConcurrentHashMap<>();
    /**
     * 用户唯一标识和channelId映射关系Map
     */
    private ConcurrentMap<String, String> uniqueChannelMap = new ConcurrentHashMap<>();

    /**
     * 添加channel到{@link #sessionIdMap}中<br>
     * 连接创建完成调用<br>
     * 
     * @param channel
     * @author yangjie
     * @date 2023/2/8
     * @return {@link Session}
     */
    public Session put(Channel channel) {
        Session session = Session.builder().id(channel.id().asLongText()).lastConnectionTime(System.currentTimeMillis())
            .channel(channel).build();
        sessionIdMap.put(channel.id().asLongText(), session);
        if (log.isInfoEnabled()) {
            log.info("[add],[一个连接{}加入]", channel.id().asLongText());
        }
        return session;
    }

    /**
     * 设备登录或鉴权后绑定用户和channelId绑定关系
     * 
     * @author yangjie
     * @date 2023/2/8
     * @param userUnique
     * @param channel
     * @return {@link boolean}
     */
    public boolean put(String userUnique, Channel channel) {
        Session session = sessionIdMap.get(channel.id().asLongText());
        if (session == null) {
            if (log.isErrorEnabled()) {
                log.error("[addUser],[连接{}不存在]", channel.id());
            }
            return false;
        }
        session.setUserUnique(userUnique);
        session.setLastConnectionTime(System.currentTimeMillis());
        session.setAuth(true);
        uniqueChannelMap.put(userUnique, channel.id().asLongText());
        session.setServerHost(System.getenv(BusinessConstants.LOCAL_OUTER_IP));
        session.setServerPort(port);
        if (channel.hasAttr(AttributeKey.valueOf(BusinessConstants.PROTOCOL_KEY))) {
            session.setProtocol(channel.attr(AttributeKey.valueOf(BusinessConstants.PROTOCOL_KEY)).get().toString());
        }
        redisTemplate.opsForValue().set(BusinessConstants.JZX_SESSION_KEY + userUnique, session, sessionKeepTime,
            TimeUnit.SECONDS);
        return true;
    }

    /**
     * 根据设备唯一标识获取Session
     * 
     * @author yangjie
     * @date 2023/2/8
     * @param unique
     * @return {@link Session}
     */
    public Session findSessionByUnique(String unique) {
        String channelId = this.uniqueChannelMap.get(unique);
        if (channelId == null) {
            return null;
        }
        return this.findSessionByChannelId(channelId);
    }

    /**
     * 根据channelId获取Session
     * 
     * @author yangjie
     * @date 2023/2/8
     * @param channelId
     * @return {@link Session}
     */
    public Session findSessionByChannelId(String channelId) {
        Session session = this.sessionIdMap.get(channelId);
        if (session == null) {
            return null;
        }
        return session;
    }

    /**
     * 根据channelId移除session
     * 
     * @author yangjie
     * @date 2023/2/8
     * @param channelId
     * @return {@link Session}
     */
    public Session remove(String channelId) {
        Session session = sessionIdMap.get(channelId);
        if (session == null) {
            if (log.isWarnEnabled()) {
                log.warn("channelId：{}不存在", channelId);
            }
            return null;
        }
        if (StrUtil.isNotBlank(session.getUserUnique())) {
            if (uniqueChannelMap.containsKey(session.getUserUnique())) {
                String channel = uniqueChannelMap.get(session.getUserUnique());
                if (StrUtil.equals(channelId, channel)) {
                    // 根据唯一标识，移除唯一标识和channelId
                    uniqueChannelMap.remove(session.getUserUnique());
                    redisTemplate.delete(BusinessConstants.JZX_SESSION_KEY + session.getUserUnique());
                }
                // TODO 发送离线通知
            }
        }
        // 根据channelId移除channel
        sessionIdMap.remove(channelId);
        if (log.isInfoEnabled()) {
            log.info("[remove][一个连接{}离开]", session.getId());
        }
        return session;
    }

    /**
     * 获取当前连接数
     *
     * @author yangjie
     * @date 2023/2/8
     * @param
     * @return {@link java.lang.Integer}
     */
    public Integer getConnectionCount() {
        return sessionIdMap.size();
    }

    /**
     * 获取当前登录或者鉴权过的连接数
     * 
     * @author yangjie
     * @date 2023/2/8
     * @version 1.0.0
     */
    public Integer getAuthUserCount() {
        return uniqueChannelMap.size();
    }

    /**
     * 向客户端发送消息
     * 
     * @param channel
     * @param arr
     * @param protocolType
     * @return
     * @throws InterruptedException
     */
    public boolean sendMsg(Channel channel, byte[] arr) throws InterruptedException {
        String data = HexUtil.format(HexUtil.encodeHexStr(arr)).toUpperCase();
        if (log.isDebugEnabled()) {
            log.debug("[server -> client],data: {}", data);
        }
        if (!channel.isActive()) {
            log.warn("[server -> client],channel is not active,channelId:{}", channel.id().asLongText());
            return false;
        }
        ChannelFuture future = channel.writeAndFlush(Unpooled.copiedBuffer(arr)).sync();
        if (!future.isSuccess()) {
            if (log.isErrorEnabled()) {
                log.error("[sendMsg],[发送数据出错:{}]", future.cause());
            }
        }
        Session session = sessionIdMap.get(channel.id().asLongText());
        String macId = "";
        if (session == null) {
            log.warn("sendMsg warn,session not find,channelId : {}", channel.id().asLongText());
        } else {
            macId = session.getUserUnique();
        }
        this.buildLogMsg(channel, macId, data);
        return true;
    }

    public void buildLogMsg(Channel ctx, String macId, String msg) {
        this.buildLogMsg(ctx, macId, msg, "[server->client]");
    }

    public void buildLogMsg(Channel ctx, String macId, String data, String target) {
        InetSocketAddress inetSocket = (InetSocketAddress)ctx.remoteAddress();
        String clientIp = inetSocket.getAddress().getHostAddress();
        Integer clientPort = inetSocket.getPort();
        ComLogDto comLogDto =
            ComLogDto.builder().sHost(NodeRegisterCluster.REDIS_QUEUE).sPort(port).sTime(System.currentTimeMillis())
                .cHost(clientIp).cPort(clientPort).macId(macId).datas(data).target(target).build();
        rabbitTemplate.convertAndSend(BusinessConstants.SYSTEM_LOG_EXCHANGE, BusinessConstants.SYSTEM_LOG_ROUTE_KEY,
            JSON.toJSONString(comLogDto));
    }
}
