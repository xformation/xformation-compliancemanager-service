/*
 * */
package com.synectiks.process.server.inputs.transports.netty;

import com.codahale.metrics.MetricRegistry;
import com.synectiks.process.server.inputs.transports.NettyTransportConfiguration;

import io.netty.channel.EventLoopGroup;

import javax.inject.Inject;
import javax.inject.Provider;

public class EventLoopGroupProvider implements Provider<EventLoopGroup> {
    private final EventLoopGroupFactory eventLoopGroupFactory;
    private final NettyTransportConfiguration configuration;
    private final MetricRegistry metricRegistry;

    @Inject
    public EventLoopGroupProvider(EventLoopGroupFactory eventLoopGroupFactory,
                                  NettyTransportConfiguration configuration,
                                  MetricRegistry metricRegistry) {
        this.eventLoopGroupFactory = eventLoopGroupFactory;
        this.configuration = configuration;
        this.metricRegistry = metricRegistry;
    }

    @Override
    public EventLoopGroup get() {
        final String name = "netty-transport";
        final int numThreads = configuration.getNumThreads();
        return eventLoopGroupFactory.create(numThreads, metricRegistry, name);
    }
}
