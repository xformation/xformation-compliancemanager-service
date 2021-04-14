/*
 * */
package com.synectiks.process.server.indexer.messages;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.synectiks.process.server.plugin.GlobalMetricNames;

import javax.inject.Inject;

public class TrafficAccounting {
    private final Counter outputByteCounter;
    private final Counter systemTrafficCounter;

    @Inject
    public TrafficAccounting(MetricRegistry metricRegistry) {
        outputByteCounter = metricRegistry.counter(GlobalMetricNames.OUTPUT_TRAFFIC);
        systemTrafficCounter = metricRegistry.counter(GlobalMetricNames.SYSTEM_OUTPUT_TRAFFIC);
    }

    public void addOutputTraffic(long size) {
        this.outputByteCounter.inc(size);
    }

    public void addSystemTraffic(long size) {
        this.systemTrafficCounter.inc(size);
    }
}
