package org.kangaroo.rocketmq.common;


import org.apache.rocketmq.logging.org.slf4j.Logger;
import org.apache.rocketmq.logging.org.slf4j.LoggerFactory;
import org.jetbrains.annotations.NotNull;
import org.kangaroo.rocketmq.common.constant.LoggerName;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author dongpengkun
 * @className ThreadFactoryImpl
 * @date 2025/8/14
 * @desc 创建线程的工厂
 */
public class ThreadFactoryImpl implements ThreadFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerName.COMMON_LOGGER_NAME);
    private final String threadNamePrefix;
    private final boolean daemon;
    private final AtomicLong threadIndex = new AtomicLong(0);

    public ThreadFactoryImpl(final String threadNamePrefix) {
        this(threadNamePrefix, false);
    }

    public ThreadFactoryImpl(final String threadNamePrefix, final boolean daemon) {
        this.threadNamePrefix = threadNamePrefix;
        this.daemon = daemon;
    }

    public ThreadFactoryImpl(final String threadNamePrefix, BrokerIdentity brokerIdentity) {
        this(threadNamePrefix, false, brokerIdentity);
    }

    public ThreadFactoryImpl(final String threadNamePrefix, final boolean daemon, BrokerIdentity brokerIdentity) {
        this.daemon = daemon;
        // 如果broker运行在容器中，线程名称前缀添加broker名称
        if (brokerIdentity != null && brokerIdentity.isInBrokerContainer()) {
            this.threadNamePrefix = brokerIdentity.getBrokerName() + "-" + threadNamePrefix;
        } else {
            this.threadNamePrefix = threadNamePrefix;
        }

    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        // 每次调用新建一个线程
        Thread thread = new Thread(r, threadNamePrefix + threadIndex.incrementAndGet());
        thread.setDaemon(daemon);

        // 任何未捕获到的异常都打印到公共日志
        thread.setUncaughtExceptionHandler((t, e) ->
                LOGGER.error("[BUG] 线程有未捕获到异常, 线程id={}, 线程名称={}", t.getId(), t.getName(), e));
        return thread;
    }
}
