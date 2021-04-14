/*
 * */
package com.synectiks.process.common.plugins.cef.input;


import com.codahale.metrics.MetricRegistry;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.common.plugins.cef.codec.CEFCodec;
import com.synectiks.process.server.inputs.transports.TcpTransport;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.plugin.inputs.annotations.ConfigClass;
import com.synectiks.process.server.plugin.inputs.annotations.FactoryClass;

import javax.inject.Inject;

public class CEFTCPInput extends MessageInput {
    private static final String NAME = "CEF TCP";

    @AssistedInject
    public CEFTCPInput(@Assisted Configuration configuration,
                       MetricRegistry metricRegistry,
                       final TcpTransport.Factory tcpTransportFactory,
                       final LocalMetricRegistry localRegistry,
                       CEFCodec.Factory codec,
                       Config config,
                       Descriptor descriptor,
                       ServerStatus serverStatus) {
        super(
                metricRegistry,
                configuration,
                tcpTransportFactory.create(configuration),
                localRegistry,
                codec.create(configuration),
                config,
                descriptor,
                serverStatus
        );
    }

    @FactoryClass
    public interface Factory extends MessageInput.Factory<CEFTCPInput> {
        @Override
        CEFTCPInput create(Configuration configuration);

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

    @ConfigClass
    public static class Config extends MessageInput.Config {
        @Inject
        public Config(TcpTransport.Factory transport, CEFCodec.Factory codec) {
            super(transport.getConfig(), codec.getConfig());
        }
    }

}
