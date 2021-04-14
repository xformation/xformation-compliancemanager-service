/*
 * */
package com.synectiks.process.server.shared.bindings.providers;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.shared.rest.resources.ProxiedResource;

import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class ProxiedRequestsExecutorService implements Provider<ExecutorService> {
    private final int proxiedRequestsMaxThreads;

    @Inject
    public ProxiedRequestsExecutorService(@Named("proxied_requests_thread_pool_size") int proxiedRequestsMaxThreads) {
        this.proxiedRequestsMaxThreads = proxiedRequestsMaxThreads;
    }

    @Override
    public ExecutorService get() {
        return Executors.newFixedThreadPool(proxiedRequestsMaxThreads,
            new ThreadFactoryBuilder()
                .setNameFormat("proxied-requests-pool-%d")
                .setDaemon(true)
                .setUncaughtExceptionHandler(new Tools.LogUncaughtExceptionHandler(LoggerFactory.getLogger(ProxiedResource.class.getName())))
                .build()
        );
    }
}
