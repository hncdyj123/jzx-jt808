package com.jzx.jt808.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.jzx.jt808.annotation.PlatFormOrderType;
import com.jzx.jt808.handler.PlatformCommonHandler;

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
public class PlatformHandlerContainer implements InitializingBean {
    /**
     * 平台下行的handler处理类容器
     */
    private final Map<Integer, PlatformCommonHandler> platformMessageHandlerMap = new ConcurrentHashMap();
    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        applicationContext.getBeansOfType(PlatformCommonHandler.class).values().forEach(messageHandler -> {
            log.warn("Platform MessageHandler found name:{}", messageHandler.getClass().getSimpleName());
            PlatFormOrderType terminalOrderType =
                messageHandler.getClass().getDeclaredAnnotation(PlatFormOrderType.class);
            if (terminalOrderType != null && terminalOrderType.initialize()) {
                platformMessageHandlerMap.put(terminalOrderType.value().getOrder(), messageHandler);
            }
        });
    }

    /**
     * 获取平台下行消息处理类
     * 
     * @author yangjie
     * @date 2023/12/25
     * @param type
     * @return {@link PlatformCommonHandler}
     */
    public PlatformCommonHandler getPlatformCommonHandler(Integer type) {
        PlatformCommonHandler handler = platformMessageHandlerMap.get(type);
        if (handler == null) {
            return null;
        }
        return handler;
    }
}
