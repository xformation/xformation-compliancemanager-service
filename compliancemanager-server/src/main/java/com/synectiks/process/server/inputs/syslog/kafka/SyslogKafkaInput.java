/*
 * */
package com.synectiks.process.server.inputs.syslog.kafka;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.server.inputs.codecs.SyslogCodec;
import com.synectiks.process.server.inputs.transports.KafkaTransport;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.plugin.inputs.annotations.ConfigClass;
import com.synectiks.process.server.plugin.inputs.annotations.FactoryClass;

import javax.inject.Inject;

public class SyslogKafkaInput extends MessageInput {
    private static final String NAME = "Syslog Kafka";

    @AssistedInject
    public SyslogKafkaInput(@Assisted Configuration configuration,
                            MetricRegistry metricRegistry,
                            KafkaTransport.Factory transport,
                            SyslogCodec.Factory codec,
                            LocalMetricRegistry localRegistry,
                            Config config,
                            Descriptor descriptor, ServerStatus serverStatus) {
        this(metricRegistry,
                configuration,
                transport.create(configuration),
                codec.create(configuration),
                localRegistry,
                config,
                descriptor, serverStatus);
    }

    protected SyslogKafkaInput(MetricRegistry metricRegistry,
                               Configuration configuration,
                               KafkaTransport kafkaTransport,
                               SyslogCodec codec,
                               LocalMetricRegistry localRegistry,
                               MessageInput.Config config,
                               MessageInput.Descriptor descriptor, ServerStatus serverStatus) {
        super(metricRegistry, configuration, kafkaTransport, localRegistry, codec, config, descriptor, serverStatus);
    }

    @FactoryClass
    public interface Factory extends MessageInput.Factory<SyslogKafkaInput> {
        @Override
        SyslogKafkaInput create(Configuration configuration);

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
        public Config(KafkaTransport.Factory transport, SyslogCodec.Factory codec) {
            super(transport.getConfig(), codec.getConfig());
        }
    }
}
