/*
 * */
package com.synectiks.process.server.indexer.searches.timeranges;

import org.joda.time.Seconds;

import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

public final class TimeRanges {
    private TimeRanges() {
    }

    /**
     * Calculate the number of seconds in the given time range.
     *
     * @param timeRange the {@link TimeRange}
     * @return the number of seconds in the given time range or 0 if an error occurred.
     */
    public static int toSeconds(TimeRange timeRange) {
        if (timeRange.getFrom() == null || timeRange.getTo() == null) {
            return 0;
        }

        try {
            return Seconds.secondsBetween(timeRange.getFrom(), timeRange.getTo()).getSeconds();
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }
}
