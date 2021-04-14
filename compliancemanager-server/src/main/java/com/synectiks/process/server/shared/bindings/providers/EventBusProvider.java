/*
 * */
package com.synectiks.process.server.shared.bindings.providers;

import com.codahale.metrics.InstrumentedExecutorService;
import com.codahale.metrics.MetricRegistry;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.synectiks.process.server.plugin.BaseConfiguration;
import com.synectiks.process.server.shared.events.DeadEventLoggingListener;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static com.codahale.metrics.MetricRegistry.name;

public class EventBusProvider implements Provider<EventBus> {
    private final BaseConfiguration configuration;
    private final MetricRegistry metricRegistry;

    @Inject
    public EventBusProvider(final BaseConfiguration configuration, final MetricRegistry metricRegistry) {
        this.configuration = configuration;
        this.metricRegistry = metricRegistry;
    }

    @Override
    public EventBus get() {
        final EventBus eventBus = new AsyncEventBus("alertmanager-eventbus", executorService(configuration.getAsyncEventbusProcessors()));
        eventBus.register(new DeadEventLoggingListener());

        return eventBus;
    }

    private ExecutorService executorService(int nThreads) {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("eventbus-handler-%d").build();
        return new InstrumentedExecutorService(
                Executors.newFixedThreadPool(nThreads, threadFactory),
                metricRegistry,
                name(this.getClass(), "executor-service"));
    }
}
