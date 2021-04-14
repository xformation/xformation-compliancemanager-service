/*
 * */
package com.synectiks.process.server.inputs.syslog.udp;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.server.inputs.codecs.SyslogCodec;
import com.synectiks.process.server.inputs.transports.UdpTransport;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.MessageInput;

import javax.inject.Inject;

public class SyslogUDPInput extends MessageInput {

    private static final String NAME = "Syslog UDP";

    @AssistedInject
    public SyslogUDPInput(MetricRegistry metricRegistry,
                          @Assisted final Configuration configuration,
                          final UdpTransport.Factory udpTransportFactory,
                          final SyslogCodec.Factory syslogCodecFactory,
                          LocalMetricRegistry localRegistry, Config config, Descriptor descriptor, ServerStatus serverStatus) {
        super(metricRegistry,
                configuration,
                udpTransportFactory.create(configuration),
                localRegistry, syslogCodecFactory.create(configuration),
                config, descriptor, serverStatus);
    }

    public interface Factory extends MessageInput.Factory<SyslogUDPInput> {
        @Override
        SyslogUDPInput create(Configuration configuration);

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
        public Config(UdpTransport.Factory transport, SyslogCodec.Factory codec) {
            super(transport.getConfig(), codec.getConfig());
        }
    }
}
