package com.jzx.jt808.config;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * mongo多数据源properties类
 * 
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.mongodb")
public class MultipleMongoProperties {
    private MongoProperties primary = new MongoProperties();
    private MongoProperties secondary = new MongoProperties();
}
