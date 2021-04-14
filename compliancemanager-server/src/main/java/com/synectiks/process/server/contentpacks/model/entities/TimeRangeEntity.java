/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.synectiks.process.server.contentpacks.jackson.ValueReferenceTypeResolverBuilder;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.InvalidRangeParametersException;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.KeywordRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.RelativeRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = TypedEntity.FIELD_META_TYPE, visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(name = AbsoluteRangeEntity.TYPE, value = AbsoluteRangeEntity.class),
        @JsonSubTypes.Type(name = RelativeRangeEntity.TYPE, value = RelativeRangeEntity.class),
        @JsonSubTypes.Type(name = KeywordRangeEntity.TYPE, value = KeywordRangeEntity.class)
})
@JsonTypeResolver(ValueReferenceTypeResolverBuilder.class)
public abstract class TimeRangeEntity implements TypedEntity {
    interface TimeRangeBuilder<SELF> extends TypedEntity.TypeBuilder<SELF> {
    }

    public static TimeRangeEntity of(TimeRange timeRange) {
        if (timeRange instanceof AbsoluteRange) {
            return AbsoluteRangeEntity.of((AbsoluteRange) timeRange);
        } else if (timeRange instanceof KeywordRange) {
            return KeywordRangeEntity.of((KeywordRange) timeRange);
        } else if (timeRange instanceof RelativeRange) {
            return RelativeRangeEntity.of((RelativeRange) timeRange);
        } else {
            throw new IllegalArgumentException("Unknown time range type " + timeRange.getClass());
        }
    }

    public abstract TimeRange convert(Map<String, ValueReference> parameters);
}
