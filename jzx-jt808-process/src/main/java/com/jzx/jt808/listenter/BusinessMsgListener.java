package com.jzx.jt808.listenter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.jzx.jt808.dao.ComLogDtoDao;
import com.jzx.jt808.entity.ComLogDto;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.jzx.jt808.BusinessConstants;
import com.jzx.jt808.protocol.jt808.Jt808HeartReq;
import com.jzx.jt808.protocol.jt808.Jt808LocationReq;
import com.jzx.jt808.protocol.jt808.Jt808LoginReq;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据监听类
 * 
 * @author yangjie
 * @date 2023/2/24
 * @version 1.0.0
 */
@Slf4j
@Component
public class BusinessMsgListener {
    @Resource
    private ComLogDtoDao commandDtoDao;

    /**
     * 设备登录数据消费
     * 
     * @author yangjie
     * @date 2023/2/24
     * @param message
     * @return
     */
    @RabbitHandler
    @RabbitListener(bindings = {
        @QueueBinding(value = @Queue(value = BusinessConstants.JT808_LOGIN_QUEUE, durable = "true", declare = "false"),
            exchange = @Exchange(value = BusinessConstants.JT808_LOGIN_EXCHANGE, type = "direct", declare = "false"),
            declare = "false")},
        containerFactory = "businessContainerFactory")
    public void processLogin(Message message) {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("BusinessMsgListener processLogin() 监听到一条消息:{}", msg);
        Jt808LoginReq loginReq = JSON.parseObject(msg, Jt808LoginReq.class);
        // 剩下业务逻辑处理
    }

    /**
     * 设备鉴权数据消费
     *
     * @author yangjie
     * @date 2023/2/24
     * @param message
     * @return
     */
    @RabbitHandler
    @RabbitListener(bindings = {
        @QueueBinding(value = @Queue(value = BusinessConstants.JT808_AUTH_QUEUE, durable = "true", declare = "false"),
            exchange = @Exchange(value = BusinessConstants.JT808_AUTH_EXCHANGE, type = "direct", declare = "false"),
            declare = "false")},
        containerFactory = "businessContainerFactory")
    public void processAuth(Message message) {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("BusinessMsgListener processAuth() 监听到一条消息:{}", msg);
        Jt808LoginReq loginReq = JSON.parseObject(msg, Jt808LoginReq.class);
        // 剩下业务逻辑处理
    }

    /**
     * 设备鉴权数据消费
     *
     * @author yangjie
     * @date 2023/2/24
     * @param message
     * @return
     */
    @RabbitHandler
    @RabbitListener(bindings = {
        @QueueBinding(value = @Queue(value = BusinessConstants.JT808_HEART_QUEUE, durable = "true", declare = "false"),
            exchange = @Exchange(value = BusinessConstants.JT808_HEART_EXCHANGE, type = "direct", declare = "false"),
            declare = "false")},
        containerFactory = "businessContainerFactory")
    public void processHeart(Message message) {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("BusinessMsgListener processHeart() 监听到一条消息:{}", msg);
        Jt808HeartReq loginReq = JSON.parseObject(msg, Jt808HeartReq.class);
        // 剩下业务逻辑处理
    }

    /**
     * 设备位置数据消费
     * 
     * @author yangjie
     * @date 2023/2/24
     * @param message
     * @return
     */
    @RabbitHandler
    @RabbitListener(bindings = {@QueueBinding(
        value = @Queue(value = BusinessConstants.JT808_LOCATION_QUEUE, durable = "true", declare = "false"),
        exchange = @Exchange(value = BusinessConstants.JT808_LOCATION_EXCHANGE, type = "direct", declare = "false"),
        declare = "false")}, containerFactory = "businessContainerFactory")
    public void processLocation(Message message) {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("BusinessMsgListener processLocation() 监听到一条消息:{}", msg);
        Jt808LocationReq locationReq = JSON.parseObject(msg, Jt808LocationReq.class);
        // 剩下业务逻辑处理
    }

    /**
     * 设备日志数据消费
     *
     * @author yangjie
     * @date 2022/11/28
     * @param message
     * @return
     */
    @RabbitHandler
    @RabbitListener(bindings = {
        @QueueBinding(value = @Queue(value = BusinessConstants.SYSTEM_LOG_QUEUE, durable = "true", declare = "false"),
            exchange = @Exchange(value = BusinessConstants.SYSTEM_LOG_EXCHANGE, type = "direct", declare = "false"),
            declare = "false")},
        containerFactory = "batchQueueRabbitListenerContainerFactory")
    public void processComLog(List<Message> messages) {
        List<ComLogDto> comLogsDtoList = new ArrayList<>();
        messages.forEach(message -> {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            comLogsDtoList.add(JSON.parseObject(message.getBody(), ComLogDto.class));
        });
        String collectionName =
            BusinessConstants.LOG_COLLECTION_NAME + DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
        commandDtoDao.insertBatch(comLogsDtoList, collectionName);
    }
}
