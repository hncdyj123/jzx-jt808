package com.jzx.jt808.factory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.NamedThreadFactory;

/**
 * 类描述：线程池工厂类
 *
 * @author yangjie
 * @date 2023-11-13 11:08
 **/
public class ExecutorFactory {
    // cpu核数
    static int core = Runtime.getRuntime().availableProcessors() < 4 ? 4 : Runtime.getRuntime().availableProcessors();
    static int maxPool = core * 2 + 1;

    private static class SingletonHolder {
        private static final ExecutorService INSTANCE = ExecutorBuilder.create().setCorePoolSize(core)
            .setMaxPoolSize(maxPool).setWorkQueue(new LinkedBlockingQueue<>(2048))
            .setThreadFactory(new NamedThreadFactory("pool-received-thread-", false)).build();
    }

    private ExecutorFactory() {}

    public static final ExecutorService getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
