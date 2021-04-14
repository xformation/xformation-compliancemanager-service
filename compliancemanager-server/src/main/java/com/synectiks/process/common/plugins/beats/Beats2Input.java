/*
 * */
package com.synectiks.process.common.plugins.beats;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.assistedinject.Assisted;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.plugin.inputs.annotations.ConfigClass;
import com.synectiks.process.server.plugin.inputs.annotations.FactoryClass;

import javax.inject.Inject;

public class Beats2Input extends MessageInput {
    private static final String NAME = "Beats";

    @Inject
    public Beats2Input(@Assisted Configuration configuration,
                       BeatsTransport.Factory transportFactory,
                       Beats2Codec.Factory codecFactory,
                       Config config,
                       Descriptor descriptor,
                       MetricRegistry metricRegistry,
                       LocalMetricRegistry localRegistry,
                       ServerStatus serverStatus) {
        super(metricRegistry, configuration, transportFactory.create(configuration),
                localRegistry, codecFactory.create(configuration), config, descriptor, serverStatus);
    }

    @FactoryClass
    public interface Factory extends MessageInput.Factory<Beats2Input> {
        @Override
        Beats2Input create(Configuration configuration);

        @Override
        Config getConfig();

        @Override
        Descriptor getDescriptor();
    }

    public static class Descriptor extends MessageInput.Descriptor {
        public Descriptor() {
            super(NAME, false, "");
        }
    }

    @ConfigClass
    public static class Config extends MessageInput.Config {
        @Inject
        public Config(BeatsTransport.Factory transport, Beats2Codec.Factory codec) {
            super(transport.getConfig(), codec.getConfig());
        }
    }
}
