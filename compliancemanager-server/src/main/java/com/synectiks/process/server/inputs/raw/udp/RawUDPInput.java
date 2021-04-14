/*
 * */
package com.synectiks.process.server.inputs.raw.udp;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.server.inputs.codecs.RawCodec;
import com.synectiks.process.server.inputs.transports.UdpTransport;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.MessageInput;

import javax.inject.Inject;

public class RawUDPInput extends MessageInput {

    private static final String NAME = "Raw/Plaintext UDP";

    @AssistedInject
    public RawUDPInput(MetricRegistry metricRegistry,
                       @Assisted final Configuration configuration,
                       final UdpTransport.Factory udpTransportFactory,
                       final RawCodec.Factory rawCodecFactory,
                       LocalMetricRegistry localRegistry,
                       Config config,
                       Descriptor descriptor, ServerStatus serverStatus) {
        super(metricRegistry, configuration, udpTransportFactory.create(configuration),
              localRegistry,
              rawCodecFactory.create(configuration), config, descriptor, serverStatus);
    }


    public interface Factory extends MessageInput.Factory<RawUDPInput> {
        @Override
        RawUDPInput create(Configuration configuration);

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
        public Config(UdpTransport.Factory transport, RawCodec.Factory codec) {
            super(transport.getConfig(), codec.getConfig());
        }
    }
}
