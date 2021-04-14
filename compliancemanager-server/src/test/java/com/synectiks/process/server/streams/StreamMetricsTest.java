/*
 * */
package com.synectiks.process.server.streams;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.synectiks.process.server.streams.StreamMetrics;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StreamMetricsTest {
    private MetricRegistry metricRegistry;
    private StreamMetrics streamMetrics;

    @Before
    public void setUp() {
        metricRegistry = new MetricRegistry();
        streamMetrics = new StreamMetrics(metricRegistry);
    }

    @Test
    public void getExecutionTimer() {
        final Timer timer = streamMetrics.getExecutionTimer("stream-id", "stream-rule-id");

        assertThat(timer).isNotNull();
        assertThat(metricRegistry.getTimers())
                .containsKey("org.graylog2.plugin.streams.Stream.stream-id.StreamRule.stream-rule-id.executionTime");
    }
}