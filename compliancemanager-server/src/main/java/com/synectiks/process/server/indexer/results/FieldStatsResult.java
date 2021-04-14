/*
 * */
package com.synectiks.process.server.indexer.results;

import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@AutoValue
public abstract class FieldStatsResult {
    public abstract long count();

    public abstract double sum();

    public abstract double sumOfSquares();

    public abstract double mean();

    public abstract double min();

    public abstract double max();

    public abstract double variance();

    public abstract double stdDeviation();

    public abstract long cardinality();

    public abstract List<ResultMessage> searchHits();

    @Nullable
    public abstract String originalQuery();

    @Nullable
    public abstract String builtQuery();

    public abstract long tookMs();

    public static FieldStatsResult create(long count,
                                          double sum,
                                          double sumOfSquares,
                                          double mean,
                                          double min,
                                          double max,
                                          double variance,
                                          double stdDeviation,
                                          long cardinality,
                                          List<ResultMessage> hits,
                                          String originalQuery,
                                          String builtQuery,
                                          long tookMs) {
        return new AutoValue_FieldStatsResult(count, sum, sumOfSquares, mean, min, max, variance, stdDeviation, cardinality,
                hits, originalQuery, builtQuery, tookMs);
    }

    public static FieldStatsResult empty(String query, String builtQuery) {
        return create(Long.MIN_VALUE, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
                Long.MIN_VALUE, Collections.emptyList(), query, builtQuery, 0);
    }
}
