package com.jzx.jt808.server.handler;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jzx.jt808.dispatcher.Jt808MessageDispatcher;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 通道数据处理拦截链handler类
 * 
 * @author yangjie
 * @date 2022/11/25
 * @version 1.0.0
 */
@Component
public class NettyServerHandlerInitializer extends ChannelInitializer<Channel> {
    @Resource
    private NettyServerHandler nettyServerHandler;
    @Resource
    private Jt808MessageDispatcher jt808MessageDispatcher;
    @Value("${business.tcpKeepTime}")
    private Integer tcpKeepTime;

    @Override
    protected void initChannel(Channel ch) {
        // 获得 Channel 对应的 ChannelPipeline
        ChannelPipeline channelPipeline = ch.pipeline();
        // 添加一堆 NettyServerHandler 到 ChannelPipeline 中
        channelPipeline
            // 空闲检测
            .addLast(new IdleStateHandler(0, 0, tcpKeepTime, TimeUnit.SECONDS))
            // 服务端处理器
            .addLast(nettyServerHandler)
            // 添加JT808协议解析器
            .addLast(new DelimiterBasedFrameDecoder(2048, Unpooled.copiedBuffer(new byte[] {0x7e}),
                Unpooled.copiedBuffer(new byte[] {0x7e})))
            // 协议选择器
            .addLast(jt808MessageDispatcher);
    }
}
