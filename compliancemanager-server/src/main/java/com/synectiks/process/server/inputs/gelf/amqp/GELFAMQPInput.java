/*
 * */
package com.synectiks.process.server.inputs.gelf.amqp;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.server.inputs.codecs.GelfCodec;
import com.synectiks.process.server.inputs.transports.AmqpTransport;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.MessageInput;

import javax.inject.Inject;

public class GELFAMQPInput extends MessageInput {

    private static final String NAME = "GELF AMQP";

    @AssistedInject
    public GELFAMQPInput(final MetricRegistry metricRegistry,
                         final @Assisted Configuration configuration,
                         final AmqpTransport.Factory amqpFactory,
                         final GelfCodec.Factory gelfCodecFactory,
                         final LocalMetricRegistry localRegistry,
                         final Config config,
                         final Descriptor descriptor,
                         final ServerStatus serverStatus) {
        super(metricRegistry, configuration, amqpFactory.create(configuration), localRegistry,
                gelfCodecFactory.create(configuration), config, descriptor, serverStatus);
    }

    public interface Factory extends MessageInput.Factory<GELFAMQPInput> {
        @Override
        GELFAMQPInput create(Configuration configuration);

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
        public Config(AmqpTransport.Factory transport, GelfCodec.Factory codec) {
            super(transport.getConfig(), codec.getConfig());
        }
    }
}
