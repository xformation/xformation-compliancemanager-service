/*
 * */
package com.synectiks.process.server.inputs.gelf.udp;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.server.inputs.codecs.GelfCodec;
import com.synectiks.process.server.inputs.transports.UdpTransport;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.MessageInput;

import javax.inject.Inject;

public class GELFUDPInput extends MessageInput {

    private static final String NAME = "GELF UDP";

    @AssistedInject
    public GELFUDPInput(MetricRegistry metricRegistry,
                        @Assisted Configuration configuration,
                        UdpTransport.Factory udpFactory,
                        GelfCodec.Factory gelfCodecFactory, LocalMetricRegistry localRegistry, Config config, Descriptor descriptor, ServerStatus serverStatus) {
        super(metricRegistry, configuration, udpFactory.create(configuration), localRegistry, gelfCodecFactory.create(configuration),
              config, descriptor, serverStatus);
    }

    public interface Factory extends MessageInput.Factory<GELFUDPInput> {
        @Override
        GELFUDPInput create(Configuration configuration);

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
        public Config(UdpTransport.Factory transport, GelfCodec.Factory codec) {
            super(transport.getConfig(), codec.getConfig());
        }
    }
}
