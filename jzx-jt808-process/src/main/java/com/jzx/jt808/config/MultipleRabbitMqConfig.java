package com.jzx.jt808.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lombok.Getter;
import lombok.Setter;

/**
 * 类描述：初始化多个rabbitmq连接示例配置类
 *
 * @author yangjie
 * @date 2022-07-22 16:23
 **/
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

    @Bean(name = "logsConnectionFactory")
    public ConnectionFactory logsConnectionFactory() {
        return createConnectionFactory(logs);
    }

    @Bean(name = "logsRabbitTemplate")
    public RabbitTemplate logsRabbitTemplate(@Qualifier("logsConnectionFactory") ConnectionFactory connectionFactory) {
        RabbitTemplate logsRabbitTemplate = new RabbitTemplate(connectionFactory);
        return logsRabbitTemplate;
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

    /**
     * 消费工厂类创建
     * 
     * @author yangjie
     * @date 2022/11/28
     * @param connectionFactory
     * @return {@link SimpleRabbitListenerContainerFactory}
     */
    @Bean(name = "businessContainerFactory")
    public SimpleRabbitListenerContainerFactory
        businessConnectionFactory(@Qualifier("businessConnectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        return factory;
    }

    /**
     * 批量消费工厂类创建
     * 
     * @author yangjie
     * @date 2022/11/28
     * @param connectionFactory
     * @return {@link SimpleRabbitListenerContainerFactory}
     */
    @Bean("batchQueueRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory batchQueueRabbitListenerContainerFactory(
        @Qualifier("logsConnectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // 设置批量
        factory.setBatchListener(true);
        // 设置BatchMessageListener生效
        factory.setConsumerBatchEnabled(true);
        // 设置监听器一次批量处理的消息数量
        factory.setBatchSize(10);
        return factory;
    }
}
