/*
 * */
package com.synectiks.process.server.shared.metrics;

import org.junit.Test;

import com.synectiks.process.server.shared.metrics.MetricUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SingleMetricFilterTest {

    @Test
    public void testMatches() throws Exception {
        final MetricUtils.SingleMetricFilter filtersAllowed = new MetricUtils.SingleMetricFilter("allowed");

        // metric is not used and can be null
        assertTrue(filtersAllowed.matches("allowed", null));
        // the match is case sensitive
        assertFalse(filtersAllowed.matches("Allowed", null));
        // the name must match
        assertFalse(filtersAllowed.matches("disallowed", null));

    }
}