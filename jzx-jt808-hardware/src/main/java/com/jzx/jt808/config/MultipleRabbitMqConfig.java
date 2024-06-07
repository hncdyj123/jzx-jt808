package com.jzx.jt808.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lombok.Getter;
import lombok.Setter;

/**
 * 初始化多个rabbitmq连接实例配置类
 * 
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class MultipleRabbitMqConfig {
    private MultipleRabbitMqProperties business = new MultipleRabbitMqProperties();
    private MultipleRabbitMqProperties logs = new MultipleRabbitMqProperties();

    @Bean(name = "businessConnectionFactory")
    @Primary
    public ConnectionFactory businessConnectionFactory() {
        return createConnectionFactory(business);
    }

    @Bean(name = "businessRabbitTemplate")
    @Primary
    public RabbitTemplate
        businessRabbitTemplate(@Qualifier("businessConnectionFactory") ConnectionFactory connectionFactory) {
        RabbitTemplate businessRabbitTemplate = new RabbitTemplate(connectionFactory);
        return businessRabbitTemplate;
    }

    @Bean(name = "businessRabbitAdmin")
    @Primary
    public RabbitAdmin
        businessRabbitAdmin(@Qualifier("businessConnectionFactory") ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean(name = "logsConnectionFactory")
    public ConnectionFactory logsConnectionFactory() {
        return createConnectionFactory(logs);
    }

    @Bean(name = "logsRabbitTemplate")
    public RabbitTemplate logsRabbitTemplate(@Qualifier("logsConnectionFactory") ConnectionFactory connectionFactory) {
        RabbitTemplate logsRabbitTemplate = new RabbitTemplate(connectionFactory);
        return logsRabbitTemplate;
    }

    @Bean(name = "logsRabbitAdmin")
    public RabbitAdmin logsRabbitAdmin(@Qualifier("logsConnectionFactory") ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    private ConnectionFactory createConnectionFactory(MultipleRabbitMqProperties rabbitMqProperties) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitMqProperties.getHost());
        connectionFactory.setPort(rabbitMqProperties.getPort());
        connectionFactory.setUsername(rabbitMqProperties.getUsername());
        connectionFactory.setPassword(rabbitMqProperties.getPassword());
        connectionFactory.setVirtualHost(rabbitMqProperties.getVirtualHost());
        return connectionFactory;
    }
}
