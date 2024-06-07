package com.jzx.jt808.dispatcher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.jzx.jt808.annotation.TerminalOrderType;

import lombok.extern.slf4j.Slf4j;

/**
 * 消息处理器容器类<br>
 * 存放所有消息的handler<br>
 * 
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
@Slf4j
@Component
public class MessageHandlerContainer implements InitializingBean {
    /**
     * 设备上行的handler处理类容器
     */
    private final Map<Integer, Jt808MessageHandler> terminalMessageHandlerMap = new ConcurrentHashMap<>();
    // /**
    // * 平台下行的handler处理类容器
    // */
    // private final Map<Integer, PlatformCommonHandler> platformMessageHandlerMap = new ConcurrentHashMap<>();

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        applicationContext.getBeansOfType(Jt808MessageHandler.class).values().forEach(messageHandler -> {
            log.info("Client MessageHandler found name:{}", messageHandler.getClass().getSimpleName());
            TerminalOrderType terminalOrderType =
                messageHandler.getClass().getDeclaredAnnotation(TerminalOrderType.class);
            if (terminalOrderType != null && terminalOrderType.initialize()) {
                terminalMessageHandlerMap.put(terminalOrderType.value().getOrder(), messageHandler);
            }
        });

        // applicationContext.getBeansOfType(PlatformCommonHandler.class).values().forEach(messageHandler -> {
        // log.info("Platform MessageHandler found name:{}", messageHandler.getClass().getSimpleName());
        // PlatFormOrderType platFormOrderType =
        // messageHandler.getClass().getDeclaredAnnotation(PlatFormOrderType.class);
        // if (platFormOrderType != null && platFormOrderType.initialize()) {
        // platformMessageHandlerMap.put(platFormOrderType.value().getOrder(), messageHandler);
        // }
        // });
    }

    /**
     * 获取设备上行消息处理类
     * 
     * @author yangjie
     * @date 2023/8/25
     * @param type 消息ID
     * @return {@link Jt808MessageHandler}
     */
    public Jt808MessageHandler getJt808MessageHandler(Integer type) {
        Jt808MessageHandler handler = terminalMessageHandlerMap.get(type);
        if (handler == null) {
            return null;
        }
        return handler;
    }

    // /**
    // * 获取平台下行消息处理类
    // *
    // * @author yangjie
    // * @date 2023/8/25
    // * @param type 消息ID
    // * @return {@link PlatformCommonHandler}
    // */
    // public PlatformCommonHandler getPlatformCommonHandler(Integer type) {
    // PlatformCommonHandler handler = platformMessageHandlerMap.get(type);
    // if (handler == null) {
    // return null;
    // }
    // return handler;
    // }
}
