package com.jzx.jt808;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 类描述：消费启动类
 *
 * @author yangjie
 * @date 2022-11-28 11:18
 **/
@Slf4j
@MapperScan("com.jzx.jt808.mapper")
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class ProcessServerApplication implements CommandLineRunner {
    private static ConfigurableApplicationContext context = null;

    public static void main(String[] args) {
        context = SpringApplication.run(ProcessServerApplication.class, args);
        Thread awaitThread = new Thread(() -> {
            long runTime = 0L;
            while (true) {
                try {
                    Thread.sleep(10000);
                    runTime += 10000;
                    log.debug("process running {} seconds", runTime / 1000);
                } catch (InterruptedException e) {
                }
            }
        });
        awaitThread.setDaemon(false);
        awaitThread.setName("awaitThread");
        awaitThread.start();
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("process context started.");
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
