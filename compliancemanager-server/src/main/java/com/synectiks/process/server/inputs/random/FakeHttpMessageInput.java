/*
 * */
package com.synectiks.process.server.inputs.random;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.server.inputs.codecs.RandomHttpMessageCodec;
import com.synectiks.process.server.inputs.transports.RandomMessageTransport;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.MessageInput;

import javax.inject.Inject;

public class FakeHttpMessageInput extends MessageInput {

    private static final String NAME = "Random HTTP message generator";

    @AssistedInject
    public FakeHttpMessageInput(@Assisted Configuration configuration,
                                RandomMessageTransport.Factory transportFactory,
                                RandomHttpMessageCodec.Factory codecFactory,
                                MetricRegistry metricRegistry, LocalMetricRegistry localRegistry, Config config, Descriptor descriptor, ServerStatus serverStatus) {
        super(metricRegistry,
                configuration,
                transportFactory.create(configuration),
                localRegistry, codecFactory.create(configuration),
                config, descriptor, serverStatus);
    }

    public interface Factory extends MessageInput.Factory<FakeHttpMessageInput> {
        @Override
        FakeHttpMessageInput create(Configuration configuration);

        @Override
        Config getConfig();

        @Override
        Descriptor getDescriptor();
    }

    public static class Descriptor extends MessageInput.Descriptor {
        @Inject
        public Descriptor() {
            super(NAME, false, "");
        }
    }

    public static class Config extends MessageInput.Config {
        @Inject
        public Config(RandomMessageTransport.Factory transport, RandomHttpMessageCodec.Factory codec) {
            super(transport.getConfig(), codec.getConfig());
        }
    }
}
