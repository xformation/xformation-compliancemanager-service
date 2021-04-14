/*
 * */
package com.synectiks.process.server.inputs.misc.jsonpath;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.server.inputs.codecs.JsonPathCodec;
import com.synectiks.process.server.inputs.transports.HttpPollTransport;
import com.synectiks.process.server.plugin.DocsHelper;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.MessageInput;

import javax.inject.Inject;

public class JsonPathInput extends MessageInput {

    private static final String NAME = "JSON path from HTTP API";

    @AssistedInject
    public JsonPathInput(@Assisted Configuration configuration,
                         HttpPollTransport.Factory transport,
                         JsonPathCodec.Factory codec,
                         MetricRegistry metricRegistry,
                         LocalMetricRegistry localRegistry, Config config, Descriptor descriptor, ServerStatus serverStatus) {
        super(metricRegistry, configuration, transport.create(configuration), localRegistry, codec.create(configuration), config,
              descriptor, serverStatus);
    }

    public interface Factory extends MessageInput.Factory<JsonPathInput> {
        @Override
        JsonPathInput create(Configuration configuration);

        @Override
        Config getConfig();

        @Override
        Descriptor getDescriptor();
    }

    public static class Descriptor extends MessageInput.Descriptor {
        @Inject
        public Descriptor() {
            super(NAME, false, DocsHelper.PAGE_SENDING_JSONPATH.toString());
        }
    }


    public static class Config extends MessageInput.Config {
        @Inject
        public Config(HttpPollTransport.Factory transport, JsonPathCodec.Factory codec) {
            super(transport.getConfig(), codec.getConfig());
        }
    }
}
