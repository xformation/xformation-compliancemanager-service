/*
 * */
package com.synectiks.process.common.plugins.netflow.inputs;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.assistedinject.Assisted;
import com.synectiks.process.common.plugins.netflow.codecs.NetFlowCodec;
import com.synectiks.process.common.plugins.netflow.transport.NetFlowUdpTransport;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.plugin.inputs.annotations.ConfigClass;
import com.synectiks.process.server.plugin.inputs.annotations.FactoryClass;

import javax.inject.Inject;

public class NetFlowUdpInput extends MessageInput {
    private static final String NAME = "NetFlow UDP";

    @Inject
    public NetFlowUdpInput(MetricRegistry metricRegistry,
                           @Assisted Configuration configuration,
                           NetFlowUdpTransport.Factory transportFactory,
                           NetFlowCodec.Factory codecFactory,
                           LocalMetricRegistry localMetricRegistry,
                           Config config,
                           Descriptor descriptor,
                           ServerStatus serverStatus) {
        super(metricRegistry, configuration, transportFactory.create(configuration), localMetricRegistry,
                codecFactory.create(configuration), config, descriptor, serverStatus);
    }

    @FactoryClass
    public interface Factory extends MessageInput.Factory<NetFlowUdpInput> {
        @Override
        NetFlowUdpInput create(Configuration configuration);

        @Override
        Config getConfig();

        @Override
        Descriptor getDescriptor();
    }

    public static class Descriptor extends MessageInput.Descriptor {
        @Inject
        public Descriptor() {
            super(NAME, false, "https://github.com/Graylog2/graylog-plugin-netflow");
        }
    }

    @ConfigClass
    public static class Config extends MessageInput.Config {
        @Inject
        public Config(NetFlowUdpTransport.Factory transport, NetFlowCodec.Factory codec) {
            super(transport.getConfig(), codec.getConfig());
        }
    }
}
