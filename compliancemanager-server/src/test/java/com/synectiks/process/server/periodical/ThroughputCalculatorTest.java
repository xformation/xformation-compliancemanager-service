/*
 * */
package com.synectiks.process.server.periodical;

import org.junit.Test;

import com.synectiks.process.server.periodical.ThroughputCalculator;

import static org.junit.Assert.assertTrue;

public class ThroughputCalculatorTest {

    @Test
    public void testStreamMetricFilter() {
        assertTrue("Filter should match stream incomingMessages", ThroughputCalculator.streamMetricFilter.matches("org.graylog2.plugin.streams.Stream.579657c468e16405f90345b0.incomingMessages", null));
    }
}
