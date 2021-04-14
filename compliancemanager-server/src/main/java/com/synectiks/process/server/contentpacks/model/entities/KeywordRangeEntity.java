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
import com.synectiks.process.server.plugin.indexer.searches.timeranges.KeywordRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import java.util.Map;

@AutoValue
@JsonAutoDetect
@JsonDeserialize(builder = AutoValue_KeywordRangeEntity.Builder.class)
public abstract class KeywordRangeEntity extends TimeRangeEntity {
    static final String TYPE = "keyword";
    private static final String FIELD_KEYWORD = "keyword";

    @JsonProperty(FIELD_KEYWORD)
    public abstract ValueReference keyword();

    public static KeywordRangeEntity of(KeywordRange keywordRange) {
        final String keyword = keywordRange.keyword();
        return builder()
                .keyword(ValueReference.of(keyword))
                .build();
    }

    static KeywordRangeEntity.Builder builder() {
        return new AutoValue_KeywordRangeEntity.Builder();
    }

    @Override
    public final TimeRange convert(Map<String, ValueReference> parameters) {
        final String keyword = keyword().asString(parameters);
        try {
            return KeywordRange.create(keyword);
        } catch (InvalidRangeParametersException e) {
            throw new RuntimeException("Invalid timerange.", e);
        }
    }

    @AutoValue.Builder
    abstract static class Builder implements TimeRangeBuilder<Builder> {
        @JsonProperty(FIELD_KEYWORD)
        abstract Builder keyword(ValueReference keyword);

        abstract KeywordRangeEntity autoBuild();

        KeywordRangeEntity build() {
            type(ModelTypeEntity.of(TYPE));
            return autoBuild();
        }
    }
}
