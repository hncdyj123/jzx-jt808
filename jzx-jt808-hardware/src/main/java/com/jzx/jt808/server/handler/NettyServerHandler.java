package com.jzx.jt808.server.handler;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.jzx.jt808.session.SessionChannelManager;
import com.jzx.jt808.session.vo.Session;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务端 Channel 实现类<br>
 * 提供对客户端 Channel 建立连接、断开连接、异常、事件处理时的处理<br>
 * | 回调方法 | 触发时机|<br>
 * | channelRegistered | 当前channel注册到EventLoop|<br>
 * | channelUnregistered | 当前channel从EventLoop取消注册|<br>
 * | channelActive | 当前channel活跃的时候|<br>
 * | channelInactive | 当前channel不活跃的时候，也就是当前channel到了它生命周期末|<br>
 * | channelRead | 当前channel从远端读取到数据|<br>
 * | channelReadComplete | channel read消费完读取的数据的时候被触发|<br>
 * | userEventTriggered | 用户事件触发的时候|<br>
 * | channelWritabilityChanged | channel的写状态变化的时候触发|<br>
 * 
 * @author yangjie
 * @date 2022/11/25
 * @version 1.0.0
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    @Resource
    private SessionChannelManager sessionManager;

    /**
     * channel活跃的时候<br>
     * 连接通道建立完成
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Session session = sessionManager.put(ctx.channel());
        if (log.isDebugEnabled()) {
            log.debug("[channelActive],[终端连接:{}]", session);
        }
        if (log.isInfoEnabled()) {
            log.info("[channelActive],[当前登录用户数为:{},当前连接数为:{},channel=>{}]", sessionManager.getAuthUserCount(),
                sessionManager.getConnectionCount(), ctx.channel().toString());
        }
    }

    /**
     * 当前channel不活跃的时候<br>
     * 服务器或者终端主动断开连接<br>
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Session session = sessionManager.findSessionByChannelId(ctx.channel().id().asLongText());
        if (log.isDebugEnabled()) {
            log.debug("[channelInactive],[终端断开连接:{}]", session);
        }
        sessionManager.remove(ctx.channel().id().asLongText());
        if (log.isInfoEnabled()) {
            log.info("[channelInactive],[当前登录用户数为:{},当前连接数为:{}]", sessionManager.getAuthUserCount(),
                sessionManager.getConnectionCount());
        }
        ctx.channel().close();
    }

    /**
     * 异常断开
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (log.isErrorEnabled()) {
            log.error("[exceptionCaught],[连接{}发生异常]", ctx.channel().id(), cause);
        }
        // 断开连接
        ctx.channel().close();
    }

    /**
     * 用户事件触发的时候<br>
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state() == IdleState.ALL_IDLE) {
                Session session = sessionManager.findSessionByChannelId(ctx.channel().id().asLongText());
                if (session != null) {
                    if (log.isWarnEnabled()) {
                        log.warn("[userEventTriggered],[服务器心跳断开连接,session:{}]", session);
                    }
                }
                if (log.isWarnEnabled()) {
                    log.info("[userEventTriggered],[当前登录用户数为:{},当前连接数为:{}]", sessionManager.getAuthUserCount(),
                        sessionManager.getConnectionCount());
                }
                ctx.close();
            }
        }
    }
}
