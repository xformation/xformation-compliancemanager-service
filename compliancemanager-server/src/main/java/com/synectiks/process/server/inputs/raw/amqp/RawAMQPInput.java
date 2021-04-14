/*
 * */
package com.synectiks.process.server.inputs.raw.amqp;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.server.inputs.codecs.RawCodec;
import com.synectiks.process.server.inputs.transports.AmqpTransport;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.MessageInput;

import javax.inject.Inject;

public class RawAMQPInput extends MessageInput {

    private static final String NAME = "Raw/Plaintext AMQP";

    @AssistedInject
    public RawAMQPInput(final MetricRegistry metricRegistry,
                        final @Assisted Configuration configuration,
                        final AmqpTransport.Factory amqpFactory,
                        final RawCodec.Factory codecFactory,
                        final LocalMetricRegistry localRegistry,
                        final Config config,
                        final Descriptor descriptor,
                        final ServerStatus serverStatus) {
        super(metricRegistry, configuration, amqpFactory.create(configuration), localRegistry,
                codecFactory.create(configuration), config, descriptor, serverStatus);
    }

    public interface Factory extends MessageInput.Factory<RawAMQPInput> {
        @Override
        RawAMQPInput create(Configuration configuration);

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
        public Config(AmqpTransport.Factory transport, RawCodec.Factory codec) {
            super(transport.getConfig(), codec.getConfig());
        }
    }
}
