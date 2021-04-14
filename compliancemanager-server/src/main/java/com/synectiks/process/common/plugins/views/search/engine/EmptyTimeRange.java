/*
 * */
package com.synectiks.process.common.plugins.views.search.engine;

import org.joda.time.DateTime;

import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

/**
 * Dummy class to allow constructing an empty {@link Query query instance}.
 */
public class EmptyTimeRange extends TimeRange {

    private static final EmptyTimeRange INSTANCE = new EmptyTimeRange();

    @Override
    public String type() {
        return "empty";
    }

    @Override
    public DateTime getFrom() {
        return null;
    }

    @Override
    public DateTime getTo() {
        return null;
    }

    public static TimeRange emptyTimeRange() {
        return INSTANCE;
    }
}
