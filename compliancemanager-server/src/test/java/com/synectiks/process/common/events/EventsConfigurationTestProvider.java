/*
 * */
package com.synectiks.process.common.events;

import com.synectiks.process.common.events.configuration.EventsConfiguration;
import com.synectiks.process.common.events.configuration.EventsConfigurationProvider;

public class EventsConfigurationTestProvider extends EventsConfigurationProvider {
    private final EventsConfiguration config;

    public static EventsConfigurationTestProvider create() {
        return new EventsConfigurationTestProvider(EventsConfiguration.builder().build());
    }

    public EventsConfigurationTestProvider(EventsConfiguration config) {
        super(null);
        this.config = config;
    }

    @Override
    public EventsConfiguration get() {
        return config;
    }
}
