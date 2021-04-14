/*
 * */
package com.synectiks.process.server.bindings;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.synectiks.process.server.Configuration;
import com.synectiks.process.server.plugin.BaseConfiguration;

import static java.util.Objects.requireNonNull;

public class ConfigurationModule implements Module {
    private final Configuration configuration;

    public ConfigurationModule(Configuration configuration) {
        this.configuration = requireNonNull(configuration);
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(Configuration.class).toInstance(configuration);
        binder.bind(BaseConfiguration.class).toInstance(configuration);
    }
}
