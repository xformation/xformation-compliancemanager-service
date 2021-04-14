/*
 * */
package com.synectiks.process.server.shared.bindings;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.synectiks.process.server.periodical.Periodicals;
import com.synectiks.process.server.plugin.Tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SchedulerBindings extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerBindings.class);
    private static final int SCHEDULED_THREADS_POOL_SIZE = 30;

    @Override
    protected void configure() {
        // TODO Add instrumentation to ExecutorService and ThreadFactory
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(SCHEDULED_THREADS_POOL_SIZE,
                new ThreadFactoryBuilder()
                        .setNameFormat("scheduled-%d")
                        .setDaemon(false)
                        .setUncaughtExceptionHandler(new Tools.LogUncaughtExceptionHandler(LOG))
                        .build()
        );

        bind(ScheduledExecutorService.class).annotatedWith(Names.named("scheduler")).toInstance(scheduler);

        // TODO Add instrumentation to ExecutorService and ThreadFactory
        final ScheduledExecutorService daemonScheduler = Executors.newScheduledThreadPool(SCHEDULED_THREADS_POOL_SIZE,
                new ThreadFactoryBuilder()
                        .setNameFormat("scheduled-daemon-%d")
                        .setDaemon(true)
                        .setUncaughtExceptionHandler(new Tools.LogUncaughtExceptionHandler(LOG))
                        .build()
        );

        bind(ScheduledExecutorService.class).annotatedWith(Names.named("daemonScheduler")).toInstance(daemonScheduler);
        bind(Periodicals.class).toInstance(new Periodicals(scheduler, daemonScheduler));
    }
}
