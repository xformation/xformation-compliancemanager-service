/*
 * */
package com.synectiks.process.server.bindings.providers;

import com.codahale.metrics.InstrumentedExecutorService;
import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.synectiks.process.server.events.ClusterEventBus;
import com.synectiks.process.server.shared.events.DeadEventLoggingListener;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static com.codahale.metrics.MetricRegistry.name;

public class ClusterEventBusProvider implements Provider<ClusterEventBus> {
    private final int asyncEventbusProcessors;
    private final MetricRegistry metricRegistry;

    @Inject
    public ClusterEventBusProvider(@Named("async_eventbus_processors") final int asyncEventbusProcessors,
                                   final MetricRegistry metricRegistry) {
        this.asyncEventbusProcessors = asyncEventbusProcessors;
        this.metricRegistry = metricRegistry;
    }

    @Override
    public ClusterEventBus get() {
        final ClusterEventBus eventBus = new ClusterEventBus("cluster-eventbus", executorService(asyncEventbusProcessors));
        eventBus.registerClusterEventSubscriber(new DeadEventLoggingListener());

        return eventBus;
    }

    private ExecutorService executorService(int nThreads) {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("cluster-eventbus-handler-%d").build();
        return new InstrumentedExecutorService(
                Executors.newFixedThreadPool(nThreads, threadFactory),
                metricRegistry,
                name("cluster-eventbus", "executor-service"));
    }
}
