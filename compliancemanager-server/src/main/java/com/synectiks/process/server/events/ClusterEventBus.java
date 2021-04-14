/*
 * */
package com.synectiks.process.server.events;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Executor;

public class ClusterEventBus extends AsyncEventBus {
    public ClusterEventBus () {
        this(MoreExecutors.directExecutor());
    }

    public ClusterEventBus(Executor executor) {
        super(executor);
    }

    public ClusterEventBus(Executor executor, SubscriberExceptionHandler subscriberExceptionHandler) {
        super(executor, subscriberExceptionHandler);
    }

    public ClusterEventBus(String identifier, Executor executor) {
        super(identifier, executor);
    }

    @Override
    public void register(Object object) {
        throw new IllegalStateException("Do not use ClusterEventBus for regular subscriptions. You probably want to use the regular EventBus.");
    }

    /**
     * Only use this if you maintain the cluster event bus! Use regular EventBus to receive cluster event updates.
     * @param object
     */
    public void registerClusterEventSubscriber(Object object) {
        super.register(object);
    }
}
