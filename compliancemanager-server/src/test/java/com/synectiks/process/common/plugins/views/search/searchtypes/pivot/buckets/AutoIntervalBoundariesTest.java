/*
 * */
package com.synectiks.process.common.plugins.views.search.searchtypes.pivot.buckets;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.buckets.AutoInterval;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.buckets.DateInterval;

import org.junit.Test;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class AutoIntervalBoundariesTest {
    @Test
    public void rangeBoundariesMustBeConnectedAndSpanEverything() {
        final ImmutableMap<Range<Duration>, DateInterval> ranges = AutoInterval.boundaries.asMapOfRanges();
        final int rangeCount = ranges.size();
        final Range<Duration> firstRange = ranges.keySet().asList().get(0);
        assertThat(firstRange.hasLowerBound()).as("first range's lower bound should be unbounded").isFalse();

        final Range<Duration> lastRange = ranges.keySet().asList().get(rangeCount - 1);
        assertThat(lastRange.hasUpperBound()).as("last range's upper bound should be unbounded").isFalse();

        final Optional<Range<Duration>> ignored = ranges
                .keySet()
                .stream()
                .reduce((r1, r2) -> {
                    assertThat(r1.isConnected(r2))
                            .as("ranges %s and %s must be connected", r1, r2)
                            .isTrue();
                    return r2;
                });
    }
}
