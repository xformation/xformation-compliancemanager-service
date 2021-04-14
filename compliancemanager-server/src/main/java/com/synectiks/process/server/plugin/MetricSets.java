/*
 * */
package com.synectiks.process.server.plugin;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class MetricSets {
    public static MetricSet of(final Map<String, ? extends Metric> gauges) {
        return new MetricSet(){
            @Override
            public Map<String, Metric> getMetrics() {
                return ImmutableMap.copyOf(gauges);
            }
        };
    }
}
