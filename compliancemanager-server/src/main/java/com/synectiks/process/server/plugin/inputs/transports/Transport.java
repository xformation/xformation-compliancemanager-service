/*
 * */
package com.synectiks.process.server.plugin.inputs.transports;

import com.codahale.metrics.MetricSet;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.plugin.inputs.MisfireException;
import com.synectiks.process.server.plugin.inputs.codecs.CodecAggregator;

public interface Transport {
    void setMessageAggregator(CodecAggregator aggregator);

    void launch(MessageInput input) throws MisfireException;

    void stop();

    MetricSet getMetricSet();

    interface Config {
        ConfigurationRequest getRequestedConfiguration();
    }

    interface Factory<T extends Transport> {
        T create(Configuration configuration);

        Config getConfig();
    }
}
