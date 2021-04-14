/*
 * */
package com.synectiks.process.server.inputs.raw.tcp;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.server.inputs.codecs.RawCodec;
import com.synectiks.process.server.inputs.transports.TcpTransport;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.MessageInput;

import javax.inject.Inject;

public class RawTCPInput extends MessageInput {

    private static final String NAME = "Raw/Plaintext TCP";

    @AssistedInject
    public RawTCPInput(@Assisted final Configuration configuration,
                       final TcpTransport.Factory tcpTransportFactory,
                       final RawCodec.Factory rawCodecFactory,
                       final MetricRegistry metricRegistry, LocalMetricRegistry localRegistry, Config config, Descriptor descriptor, ServerStatus serverStatus) {
        super(metricRegistry,
                configuration,
                tcpTransportFactory.create(configuration),
                localRegistry, rawCodecFactory.create(configuration),
                config, descriptor, serverStatus);
    }

    public interface Factory extends MessageInput.Factory<RawTCPInput> {
        @Override
        RawTCPInput create(Configuration configuration);

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
        public Config(TcpTransport.Factory transport, RawCodec.Factory codec) {
            super(transport.getConfig(), codec.getConfig());
        }
    }
}
