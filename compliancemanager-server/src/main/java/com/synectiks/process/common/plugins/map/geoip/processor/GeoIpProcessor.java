/*
 * */
package com.synectiks.process.common.plugins.map.geoip.processor;

import com.codahale.metrics.MetricRegistry;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.synectiks.process.common.plugins.map.config.GeoIpResolverConfig;
import com.synectiks.process.common.plugins.map.geoip.GeoIpResolverEngine;
import com.synectiks.process.server.cluster.ClusterConfigChangedEvent;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.Messages;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.messageprocessors.MessageProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class GeoIpProcessor implements MessageProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(GeoIpProcessor.class);

    public static class Descriptor implements MessageProcessor.Descriptor {
        @Override
        public String name() {
            return "GeoIP Resolver";
        }

        @Override
        public String className() {
            return GeoIpProcessor.class.getCanonicalName();
        }
    }

    private final ClusterConfigService clusterConfigService;
    private final ScheduledExecutorService scheduler;
    private final MetricRegistry metricRegistry;

    private final AtomicReference<GeoIpResolverConfig> config;
    private final AtomicReference<GeoIpResolverEngine> filterEngine;

    @Inject
    public GeoIpProcessor(ClusterConfigService clusterConfigService,
                          @Named("daemonScheduler") ScheduledExecutorService scheduler,
                          EventBus eventBus,
                          MetricRegistry metricRegistry) {
        this.clusterConfigService = clusterConfigService;
        this.scheduler = scheduler;
        this.metricRegistry = metricRegistry;
        final GeoIpResolverConfig config = clusterConfigService.getOrDefault(GeoIpResolverConfig.class,
                GeoIpResolverConfig.defaultConfig());

        this.config = new AtomicReference<>(config);
        this.filterEngine = new AtomicReference<>(new GeoIpResolverEngine(config, metricRegistry));

        eventBus.register(this);
    }

    @Override
    public Messages process(Messages messages) {
        for (Message message : messages) {
            filterEngine.get().filter(message);
        }

        return messages;
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void updateConfig(ClusterConfigChangedEvent event) {
        if (!GeoIpResolverConfig.class.getCanonicalName().equals(event.type())) {
            return;
        }

        scheduler.schedule((Runnable) this::reload, 0, TimeUnit.SECONDS);
    }

    private void reload() {
        final GeoIpResolverConfig newConfig = clusterConfigService.getOrDefault(GeoIpResolverConfig.class,
                GeoIpResolverConfig.defaultConfig());

        LOG.info("Updating GeoIP resolver engine - {}", newConfig);
        config.set(newConfig);
        filterEngine.set(new GeoIpResolverEngine(newConfig, metricRegistry));
    }
}
