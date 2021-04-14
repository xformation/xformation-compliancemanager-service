/*
 * */
package com.synectiks.process.server.streams;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.collect.Maps;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.plugin.streams.StreamRule;

import javax.inject.Inject;
import java.util.Map;

public class StreamMetrics {
    private final MetricRegistry metricRegistry;

    private final Map<String, Meter> streamIncomingMeters = Maps.newHashMap();
    private final Map<String, Timer> streamExecutionTimers = Maps.newHashMap();
    private final Map<String, Meter> streamExceptionMeters = Maps.newHashMap();
    private final Map<String, Meter> streamRuleTimeoutMeters = Maps.newHashMap();
    private final Map<String, Meter> streamFaultsExceededMeters = Maps.newHashMap();


    @Inject
    public StreamMetrics(final MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public void markIncomingMeter(String streamId) {
        getIncomingMeter(streamId).mark();
    }

    public Timer getExecutionTimer(String streamId, String streamRuleId) {
        Timer timer = this.streamExecutionTimers.get(streamRuleId);
        if (timer == null) {
            timer = metricRegistry.timer(MetricRegistry.name(Stream.class, streamId, "StreamRule", streamRuleId, "executionTime"));
            this.streamExecutionTimers.put(streamRuleId, timer);
        }

        return timer;
    }


    public void markExceptionMeter(String streamId) {
        getExceptionMeter(streamId).mark();
    }

    public void markStreamRuleTimeout(String streamId) {
        getStreamRuleTimeoutMeter(streamId).mark();
    }

    public void markStreamFaultsExceeded(String streamId) {
        getStreamFaultsExceededMeter(streamId).mark();
    }

    private Meter getIncomingMeter(String streamId) {
        Meter meter = this.streamIncomingMeters.get(streamId);
        if (meter == null) {
            meter = metricRegistry.meter(MetricRegistry.name(Stream.class, streamId, "incomingMessages"));
            this.streamIncomingMeters.put(streamId, meter);
        }

        return meter;
    }

    private Meter getExceptionMeter(String streamId) {
        Meter meter = this.streamExceptionMeters.get(streamId);
        if (meter == null) {
            meter = metricRegistry.meter(MetricRegistry.name(Stream.class, streamId, "matchingExceptions"));
            this.streamExceptionMeters.put(streamId, meter);
        }

        return meter;
    }

    private Meter getStreamRuleTimeoutMeter(final String streamId) {
        Meter meter = this.streamRuleTimeoutMeters.get(streamId);
        if (meter == null) {
            meter = metricRegistry.meter(MetricRegistry.name(Stream.class, streamId, "ruleTimeouts"));
            this.streamRuleTimeoutMeters.put(streamId, meter);
        }

        return meter;
    }

    private Meter getStreamFaultsExceededMeter(final String streamId) {
        Meter meter = this.streamFaultsExceededMeters.get(streamId);
        if (meter == null) {
            meter = metricRegistry.meter(MetricRegistry.name(Stream.class, streamId, "faultsExceeded"));
            this.streamFaultsExceededMeters.put(streamId, meter);
        }

        return meter;
    }
}
