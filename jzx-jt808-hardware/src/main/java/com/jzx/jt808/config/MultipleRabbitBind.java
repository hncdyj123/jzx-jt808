package com.jzx.jt808.login;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jzx.jt808.BusinessConstants;

/**
 * RabbitMQ配置类
 * 
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
@Configuration
public class MultipleRabbitBind {
    /**
     * 声明心跳消息投递队列
     * 
     * @author yangjie
     * @date 2022/11/25
     * @param
     * @return {@link Queue}
     */
    public Queue heartQueue() {
        return QueueBuilder.durable(BusinessConstants.JT808_HEART_QUEUE).build();
    }

    /**
     * 声明心跳消息投递交换机
     * 
     * @author yangjie
     * @date 2022/11/25
     * @version 1.0.0
     */
    public Exchange heartExchange() {
        return ExchangeBuilder.directExchange(BusinessConstants.JT808_HEART_EXCHANGE).durable(true).build();
    }

    /**
     * 绑定心跳消息投递队列和交换机
     * 
     * @author yangjie
     * @date 2022/11/25
     * @param
     * @return {@link Binding}
     */
    public Binding heartBinding() {
        return BindingBuilder.bind(heartQueue()).to(heartExchange()).with(BusinessConstants.JT808_HEART_ROUTE_KEY)
            .noargs();
    }

    /**
     * 声明心跳自动创建AmqpAdmin
     * 
     * @author yangjie
     * @date 2022/11/25
     * @param amqpAdmin @Primary标注的AmqpAdmin
     * @return {@link AmqpAdmin}
     */
    @Bean("heartAdmin")
    public AmqpAdmin heartAdmin(AmqpAdmin amqpAdmin) {
        amqpAdmin.declareQueue(heartQueue());
        amqpAdmin.declareExchange(heartExchange());
        amqpAdmin.declareBinding(heartBinding());
        return amqpAdmin;
    }

    /**
     * 声明登录消息投递队列
     *
     * @return
     * @see
     */
    public Queue loginQueue() {
        return QueueBuilder.durable(BusinessConstants.JT808_LOGIN_QUEUE).build();
    }

    /**
     * 声明登录相关消息投递交换机
     * 
     * @return
     * @see
     */
    public Exchange loginExchange() {
        return ExchangeBuilder.directExchange(BusinessConstants.JT808_LOGIN_EXCHANGE).durable(true).build();
    }

    /**
     * 绑定登录相关消息投递队列和交换机
     * 
     * @param queue 队列Bean
     * @param exchange 交换机Bean
     * @return
     * @see
     */
    public Binding loginBinding() {
        return BindingBuilder.bind(loginQueue()).to(loginExchange()).with(BusinessConstants.JT808_LOGIN_ROUTE_KEY)
            .noargs();
    }

    /**
     * 声明登录自动创建AmqpAdmin
     *
     * @param amqpAdmin @Primary标注的AmqpAdmin
     * @param queue
     * @param exchange
     * @param binding
     * @return
     */
    @Bean("loginAdmin")
    public AmqpAdmin loginAdmin(AmqpAdmin amqpAdmin) {
        amqpAdmin.declareQueue(loginQueue());
        amqpAdmin.declareExchange(loginExchange());
        amqpAdmin.declareBinding(loginBinding());
        return amqpAdmin;
    }

    /**
     * 声明鉴权消息投递队列
     *
     * @return
     * @see
     */
    public Queue authQueue() {
        return QueueBuilder.durable(BusinessConstants.JT808_AUTH_QUEUE).build();
    }

    /**
     * 声明鉴权相关消息投递交换机
     *
     * @return
     * @see
     */
    public Exchange authExchange() {
        return ExchangeBuilder.directExchange(BusinessConstants.JT808_AUTH_EXCHANGE).durable(true).build();
    }

    /**
     * 绑定鉴权相关消息投递队列和交换机
     *
     * @param queue 队列Bean
     * @param exchange 交换机Bean
     * @return
     * @see
     */
    public Binding authBinding() {
        return BindingBuilder.bind(authQueue()).to(authExchange()).with(BusinessConstants.JT808_AUTH_ROUTE_KEY)
            .noargs();
    }

    /**
     * 声明鉴权自动创建AmqpAdmin
     *
     * @param amqpAdmin @Primary标注的AmqpAdmin
     * @param queue
     * @param exchange
     * @param binding
     * @return
     */
    @Bean("authAdmin")
    public AmqpAdmin authAdmin(AmqpAdmin amqpAdmin) {
        amqpAdmin.declareQueue(authQueue());
        amqpAdmin.declareExchange(authExchange());
        amqpAdmin.declareBinding(authBinding());
        return amqpAdmin;
    }

    /**
     * 声明位置业务消息投递队列
     *
     * @return
     * @see
     */
    public Queue locationQueue() {
        return QueueBuilder.durable(BusinessConstants.JT808_LOCATION_QUEUE).build();
    }

    /**
     * 声明位置业务消息投递交换机
     * 
     * @return
     * @see
     */
    public Exchange locationExchange() {
        return ExchangeBuilder.directExchange(BusinessConstants.JT808_LOCATION_EXCHANGE).durable(true).build();
    }

    /**
     * 绑定位置业务消息投递队列和交换机
     * 
     * @param queue 队列Bean
     * @param exchange 交换机Bean
     * @return
     * @see
     */
    public Binding locationBinding() {
        return BindingBuilder.bind(locationQueue()).to(locationExchange())
            .with(BusinessConstants.JT808_LOCATION_ROUTE_KEY).noargs();
    }

    /**
     * 声明位置业务消息自动创建AmqpAdmin
     * 
     * @author yangjie
     * @date 2022/8/22
     * @param amqpAdmin
     * @return {@link AmqpAdmin}
     */
    @Bean("locationAdmin")
    public AmqpAdmin locationAdmin(AmqpAdmin amqpAdmin) {
        amqpAdmin.declareQueue(locationQueue());
        amqpAdmin.declareExchange(locationExchange());
        amqpAdmin.declareBinding(locationBinding());
        return amqpAdmin;
    }

    /**
     * 声明日志业务消息投递队列
     *
     * @return
     * @see
     */
    public Queue logQueue() {
        return QueueBuilder.durable(BusinessConstants.SYSTEM_LOG_QUEUE).build();
    }

    /**
     * 声明日志业务消息投递交换机
     * 
     * @return
     * @see
     */
    public Exchange logExchange() {
        return ExchangeBuilder.directExchange(BusinessConstants.SYSTEM_LOG_EXCHANGE).durable(true).build();
    }

    /**
     * 绑定日志业务消息投递队列和交换机
     * 
     * @param queue 队列Bean
     * @param exchange 交换机Bean
     * @return
     * @see
     */
    public Binding logBinding() {
        return BindingBuilder.bind(logQueue()).to(logExchange()).with(BusinessConstants.SYSTEM_LOG_ROUTE_KEY).noargs();
    }

    /**
     * 声明日志自动创建AmqpAdmin
     * 
     * @author yangjie
     * @date 2022/8/22
     * @param amqpAdmin
     * @return {@link AmqpAdmin}
     */
    @Bean("logAdmin")
    public AmqpAdmin logAdmin(@Qualifier("logsRabbitAdmin") AmqpAdmin amqpAdmin) {
        amqpAdmin.declareQueue(logQueue());
        amqpAdmin.declareExchange(logExchange());
        amqpAdmin.declareBinding(logBinding());
        return amqpAdmin;
    }
}
