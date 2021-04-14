/*
 * */
package com.synectiks.process.server.plugin.dashboards.widgets;

import com.google.common.collect.Maps;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;

import org.joda.time.DateTime;

import java.util.Map;

public class ComputationResult {

    private final Object result;
    private final long tookMs;
    private final DateTime calculatedAt;
    private final AbsoluteRange computationTimeRange;

    public ComputationResult(Object result, long tookMs) {
        this(result, tookMs, null);
    }

    public ComputationResult(Object result, long tookMs, AbsoluteRange computationTimeRange) {
        this.result = result;
        this.tookMs = tookMs;
        this.computationTimeRange = computationTimeRange;
        this.calculatedAt = Tools.nowUTC();
    }

    public Object getResult() {
        return result;
    }

    public DateTime getCalculatedAt() {
        return calculatedAt;
    }

    public long getTookMs() {
        return tookMs;
    }

    public Map<String, Object> asMap() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("result", result);
        map.put("calculated_at", Tools.getISO8601String(calculatedAt));
        map.put("took_ms", tookMs);

        if (computationTimeRange != null) {
            Map<String, Object> timeRangeMap = Maps.newHashMap();
            timeRangeMap.put("from", Tools.getISO8601String(computationTimeRange.getFrom()));
            timeRangeMap.put("to", Tools.getISO8601String(computationTimeRange.getTo()));
            map.put("computation_time_range", timeRangeMap);
        }

        return map;
    }

}
