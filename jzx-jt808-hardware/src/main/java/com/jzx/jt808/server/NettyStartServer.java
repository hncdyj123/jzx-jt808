package com.jzx.jt808.server;

import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jzx.jt808.server.handler.NettyServerHandlerInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * netty启动类
 * 
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
@Slf4j
@Component
public class NettyStartServer {
    @Value("${business.netty.port}")
    private Integer port;

    @Resource
    private NettyServerHandlerInitializer nettyServerHandlerInitializer;

    /**
     * boss 线程组，用于服务端接受客户端的连接
     */
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    /**
     * worker 线程组，用于服务端接受客户端的数据读写
     */
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    /**
     * Netty Server Channel
     */
    private Channel channel;

    /**
     * 启动 Netty Server
     */
    @PostConstruct
    public void start() throws InterruptedException {
        // 创建 ServerBootstrap 对象，用于 Netty Server 启动
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 设置 ServerBootstrap 的各种属性 设置两个 EventLoopGroup 对象
        bootstrap.group(bossGroup, workerGroup)
            // 指定 Channel 为服务端 NioServerSocketChannel
            .channel(NioServerSocketChannel.class)
            // 设置 Netty Server 的端口
            .localAddress(new InetSocketAddress(port))
            // 服务端 accept 队列的大小
            .option(ChannelOption.SO_BACKLOG, 1024)
            // TCP Keepalive 机制，实现 TCP 层级的心跳保活功能
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            // 允许较小的数据包的发送，降低延迟
            .childOption(ChannelOption.TCP_NODELAY, true).childHandler(nettyServerHandlerInitializer);
        // 绑定端口，并同步等待成功，即启动服务端
        ChannelFuture future = bootstrap.bind().sync();
        if (future.isSuccess()) {
            channel = future.channel();
            log.info("[start][Netty Server 启动在 {} 端口]", port);
        }

    }

    /**
     * 关闭 Netty Server
     */
    @PreDestroy
    public void shutdown() {
        if (channel != null) {
            channel.close();
        }
        // 优雅关闭两个 EventLoopGroup 对象
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
