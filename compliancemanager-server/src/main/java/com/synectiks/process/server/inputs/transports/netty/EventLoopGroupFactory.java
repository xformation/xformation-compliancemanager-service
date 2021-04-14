/*
 * */
package com.synectiks.process.server.inputs.transports.netty;

import com.codahale.metrics.InstrumentedExecutorService;
import com.codahale.metrics.InstrumentedThreadFactory;
import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.synectiks.process.server.inputs.transports.NettyTransportConfiguration;
import com.synectiks.process.server.plugin.LocalMetricRegistry;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import javax.inject.Inject;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class EventLoopGroupFactory {
    private final NettyTransportConfiguration configuration;

    @Inject
    public EventLoopGroupFactory(NettyTransportConfiguration configuration) {
        this.configuration = configuration;
    }

    public EventLoopGroup create(int numThreads, MetricRegistry metricRegistry, String metricPrefix) {
        final ThreadFactory threadFactory = threadFactory(metricPrefix, metricRegistry);
        final Executor executor = executor(metricPrefix, numThreads, threadFactory, metricRegistry);

        switch (configuration.getType()) {
            case EPOLL:
                return epollEventLoopGroup(numThreads, executor);
            case KQUEUE:
                return kqueueEventLoopGroup(numThreads, executor);
            case NIO:
                return nioEventLoopGroup(numThreads, executor);
            default:
                throw new RuntimeException("Invalid or unknown netty transport type " + configuration.getType());
        }
    }

    private ThreadFactory threadFactory(String name, MetricRegistry metricRegistry) {
        final String threadFactoryMetricName = MetricRegistry.name(name, "thread-factory");
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("netty-transport-%d").build();
        return new InstrumentedThreadFactory(threadFactory, metricRegistry, threadFactoryMetricName);

    }

    private Executor executor(final String name, int numThreads, final ThreadFactory threadFactory, final MetricRegistry metricRegistry) {
        final String executorMetricName = LocalMetricRegistry.name(name, "executor-service");
        final ExecutorService cachedThreadPool = Executors.newFixedThreadPool(numThreads, threadFactory);
        return new InstrumentedExecutorService(cachedThreadPool, metricRegistry, executorMetricName);
    }

    private EventLoopGroup nioEventLoopGroup(int numThreads, Executor executor) {
        return new NioEventLoopGroup(numThreads, executor);
    }


    private EventLoopGroup epollEventLoopGroup(int numThreads, Executor executor) {
        return new EpollEventLoopGroup(numThreads, executor);
    }

    private EventLoopGroup kqueueEventLoopGroup(int numThreads, Executor executor) {
        return new KQueueEventLoopGroup(numThreads, executor);
    }
}
