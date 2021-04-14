/*
 * */
package com.synectiks.process.common.events.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.plugin.cluster.ClusterConfigService;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.constraints.NotNull;
import java.util.Optional;

public class EventsConfigurationProvider implements Provider<EventsConfiguration> {
    private static final Logger LOG = LoggerFactory.getLogger(EventsConfigurationProvider.class);

    private final ClusterConfigService clusterConfigService;

    @Inject
    public EventsConfigurationProvider(ClusterConfigService clusterConfigService) {
        this.clusterConfigService = clusterConfigService;
    }

    @Override
    public EventsConfiguration get() {
        return loadFromDatabase().orElse(getDefaultConfig());
    }

    public EventsConfiguration getDefaultConfig() {
        return EventsConfiguration.builder().build();
    }

    @NotNull
    public Optional<EventsConfiguration> loadFromDatabase() {
        try {
            return Optional.ofNullable(clusterConfigService.get(EventsConfiguration.class));
        } catch (Exception e) {
            LOG.error("Failed to fetch events configuration from database", e);
            return Optional.empty();
        }
    }
}
