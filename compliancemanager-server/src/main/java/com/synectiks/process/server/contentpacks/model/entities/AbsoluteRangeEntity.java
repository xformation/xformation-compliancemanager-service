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
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import org.joda.time.format.ISODateTimeFormat;

import java.util.Map;

@AutoValue
@JsonAutoDetect
@JsonDeserialize(builder = AutoValue_AbsoluteRangeEntity.Builder.class)
public abstract class AbsoluteRangeEntity extends TimeRangeEntity {
    static final String TYPE = "absolute";
    private static final String FIELD_FROM = "from";
    private static final String FIELD_TO = "to";

    @JsonProperty(FIELD_FROM)
    public abstract ValueReference from();

    @JsonProperty(FIELD_TO)
    public abstract ValueReference to();

    public static AbsoluteRangeEntity of(AbsoluteRange absoluteRange) {
        final String from = absoluteRange.from().toString(ISODateTimeFormat.dateTime());
        final String to = absoluteRange.to().toString(ISODateTimeFormat.dateTime());
        return builder()
                .from(ValueReference.of(from))
                .to(ValueReference.of(to))
                .build();
    }

    static AbsoluteRangeEntity.Builder builder() {
        return new AutoValue_AbsoluteRangeEntity.Builder();
    }

    @Override
    public final TimeRange convert(Map<String, ValueReference> parameters) {
        final String from = from().asString(parameters);
        final String to = to().asString(parameters);
        try {
            return AbsoluteRange.create(from, to);
        } catch (InvalidRangeParametersException e) {
            throw new RuntimeException("Invalid timerange.", e);
        }
    }

    @AutoValue.Builder
    abstract static class Builder implements TimeRangeBuilder<Builder> {
        @JsonProperty(FIELD_FROM)
        abstract Builder from(ValueReference from);

        @JsonProperty(FIELD_TO)
        abstract Builder to(ValueReference to);

        abstract AbsoluteRangeEntity autoBuild();

        AbsoluteRangeEntity build() {
            type(ModelTypeEntity.of(TYPE));
            return autoBuild();
        }
    }
}
