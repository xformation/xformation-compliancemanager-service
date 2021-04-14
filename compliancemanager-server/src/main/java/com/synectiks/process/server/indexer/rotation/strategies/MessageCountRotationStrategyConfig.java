/*
 * */
package com.synectiks.process.server.indexer.rotation.strategies;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategyConfig;

import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.Min;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class MessageCountRotationStrategyConfig implements RotationStrategyConfig {
    private static final int DEFAULT_MAX_DOCS_PER_INDEX = 20_000_000;

    @JsonProperty("max_docs_per_index")
    public abstract int maxDocsPerIndex();

    @JsonCreator
    public static MessageCountRotationStrategyConfig create(@JsonProperty(TYPE_FIELD) String type,
                                                            @JsonProperty("max_docs_per_index") @Min(1) int maxDocsPerIndex) {
        return new AutoValue_MessageCountRotationStrategyConfig(type, maxDocsPerIndex);
    }

    @JsonCreator
    public static MessageCountRotationStrategyConfig create(@JsonProperty("max_docs_per_index") @Min(1) int maxDocsPerIndex) {
        return create(MessageCountRotationStrategyConfig.class.getCanonicalName(), maxDocsPerIndex);
    }

    public static MessageCountRotationStrategyConfig createDefault() {
        return create(DEFAULT_MAX_DOCS_PER_INDEX);
    }
}
