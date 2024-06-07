package com.jzx.jt808.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 初始化多个redis实例配置类
 * 
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class MultipleRedisConfig {
    private MultipleRedisProperties primary = new MultipleRedisProperties();
    private MultipleRedisProperties secondary = new MultipleRedisProperties();

    @Bean("redisTemplate")
    @Primary
    public RedisTemplate redisPrimaryTemplate() {
        return createRedisTemplate(primary);
    }

    @Bean("slaveRedisTemplate")
    public RedisTemplate redisSecondaryTemplate() {
        return createRedisTemplate(secondary);
    }

    private RedisTemplate createRedisTemplate(MultipleRedisProperties redisConfig) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setTestOnBorrow(true);
        poolConfig.setMaxTotal(redisConfig.getPoolMaxActive() == null ? 10 : redisConfig.getPoolMaxActive());
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle() == null ? 5 : redisConfig.getPoolMaxIdle());
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait() == null ? 2000L : redisConfig.getPoolMaxWait());
        poolConfig.setMinIdle(redisConfig.getPoolMinIdle() == null ? 5 : redisConfig.getPoolMaxIdle());

        RedisStandaloneConfiguration redisStandaloneConfiguration =
            new RedisStandaloneConfiguration(redisConfig.getHost(), redisConfig.getPort());
        redisStandaloneConfiguration.setPassword(redisConfig.getPassword());
        redisStandaloneConfiguration.setDatabase(redisConfig.getDatabase() == null ? 0 : redisConfig.getDatabase());

        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfigurationBuilder =
            JedisClientConfiguration.builder();
        JedisClientConfiguration jedisClientConfiguration =
            jedisClientConfigurationBuilder.usePooling().poolConfig(poolConfig).build();

        JedisConnectionFactory connectionFactory =
            new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        // key value 序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        // hashKey hashValue 序列化方式
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return redisTemplate;
    }
}
