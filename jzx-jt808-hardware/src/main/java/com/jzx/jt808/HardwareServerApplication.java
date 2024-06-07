package com.jzx.jt808;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import lombok.extern.slf4j.Slf4j;

/**
 * SpringBoot启动类
 * 
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
@Slf4j
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
public class HardwareServerApplication implements CommandLineRunner {
    private static ConfigurableApplicationContext context = null;

    public static void main(String[] args) {
        context = SpringApplication.run(HardwareServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("hardware context started.");
        // 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 执行收尾工作
            log.info("begin close application ~");
            if (context != null) {
                context.close();
            }
            log.info("shutdown finish ~");
        }));
    }
}
