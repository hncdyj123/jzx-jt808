package com.jzx.jt808;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication(exclude = {RedisAutoConfiguration.class, MongoAutoConfiguration.class})
public class WebServerApplication implements WebMvcConfigurer {
    private static ConfigurableApplicationContext context = null;

    public static void main(String[] args) {
        context = SpringApplication.run(WebServerApplication.class, args);
        log.info("web context started.");
        // 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 执行收尾工作
            log.info("web begin close application ~");
            if (context != null) {
                context.close();
            }
            log.info("web shutdown finish ~");
        }));
    }
}
