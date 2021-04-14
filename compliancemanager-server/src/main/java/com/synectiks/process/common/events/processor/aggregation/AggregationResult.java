/*
 * */
package com.synectiks.process.common.events.processor.aggregation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;

import java.util.List;
import java.util.Set;

@AutoValue
@JsonDeserialize(builder = AggregationResult.Builder.class)
public abstract class AggregationResult {
    private static final AggregationResult EMPTY_AGGREGATION_RESULT = builder()
            .keyResults(ImmutableList.of())
            .effectiveTimerange(AbsoluteRange.create(Tools.nowUTC(), Tools.nowUTC()))
            .totalAggregatedMessages(0)
            .sourceStreams(ImmutableSet.of())
            .build();

    public abstract ImmutableList<AggregationKeyResult> keyResults();

    public abstract AbsoluteRange effectiveTimerange();

    public abstract long totalAggregatedMessages();

    public abstract Set<String> sourceStreams();

    public static Builder builder() {
        return Builder.create();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public static abstract class Builder {
        @JsonCreator
        public static Builder create() {
            return new AutoValue_AggregationResult.Builder();
        }

        public abstract Builder keyResults(List<AggregationKeyResult> keyResults);

        public abstract Builder effectiveTimerange(AbsoluteRange effectiveTimerange);

        public abstract Builder totalAggregatedMessages(long total);

        public abstract Builder sourceStreams(Set<String> sourceStreams);

        public abstract AggregationResult build();
    }

    public static AggregationResult empty() {
        return EMPTY_AGGREGATION_RESULT;
    }
}
