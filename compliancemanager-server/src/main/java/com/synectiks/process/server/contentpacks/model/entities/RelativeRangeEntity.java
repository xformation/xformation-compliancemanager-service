/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.InvalidRangeParametersException;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.RelativeRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import java.util.Map;

@AutoValue
@JsonAutoDetect
@JsonDeserialize(builder = AutoValue_RelativeRangeEntity.Builder.class)
public abstract class RelativeRangeEntity extends TimeRangeEntity {
    static final String TYPE = "relative";
    private static final String FIELD_RANGE = "range";

    @JsonProperty(FIELD_RANGE)
    public abstract ValueReference range();

    public static RelativeRangeEntity of(RelativeRange relativeRange) {
        final int range = relativeRange.getRange();
        return builder()
                .range(ValueReference.of(range))
                .build();
    }

    @Override
    public final TimeRange convert(Map<String, ValueReference> parameters) {
        final int range = range().asInteger(parameters);
        try {
            return RelativeRange.create(range);
        } catch (InvalidRangeParametersException e) {
            throw new RuntimeException("Invalid timerange.", e);
        }
    }

    static RelativeRangeEntity.Builder builder() {
        return new AutoValue_RelativeRangeEntity.Builder();
    }

    @AutoValue.Builder
    abstract static class Builder implements TimeRangeBuilder<Builder> {
        @JsonProperty(FIELD_RANGE)
        abstract Builder range(ValueReference range);

        abstract RelativeRangeEntity autoBuild();

        RelativeRangeEntity build() {
            type(ModelTypeEntity.of(TYPE));
            return autoBuild();
        }
    }
}
