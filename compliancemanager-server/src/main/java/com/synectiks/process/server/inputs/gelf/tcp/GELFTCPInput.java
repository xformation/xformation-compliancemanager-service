/*
 * */
package com.synectiks.process.server.inputs.gelf.tcp;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.server.inputs.codecs.GelfCodec;
import com.synectiks.process.server.inputs.transports.TcpTransport;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.MessageInput;

import javax.inject.Inject;

public class GELFTCPInput extends MessageInput {

    private static final String NAME = "GELF TCP";

    @AssistedInject
    public GELFTCPInput(MetricRegistry metricRegistry,
                        @Assisted Configuration configuration,
                        TcpTransport.Factory tcpFactory,
                        GelfCodec.Factory gelfCodecFactory,
                        LocalMetricRegistry localRegistry,
                        Config config,
                        Descriptor descriptor, ServerStatus serverStatus) {
        super(metricRegistry, configuration, tcpFactory.create(overrideDelimiter(configuration)), localRegistry, gelfCodecFactory.create(configuration),
              config, descriptor, serverStatus);
    }

    // Make sure that delimiter is null-byte for GELF. This is needed to support setups where the GELF TCP input
    // has been created with the wrong value.
    private static Configuration overrideDelimiter(Configuration configuration) {
        configuration.setBoolean(TcpTransport.CK_USE_NULL_DELIMITER, true);

        return configuration;
    }

    public interface Factory extends MessageInput.Factory<GELFTCPInput> {
        @Override
        GELFTCPInput create(Configuration configuration);

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
        public Config(TcpTransport.Factory transport, GelfCodec.Factory codec) {
            super(transport.getConfig(), codec.getConfig());
        }
    }
}
