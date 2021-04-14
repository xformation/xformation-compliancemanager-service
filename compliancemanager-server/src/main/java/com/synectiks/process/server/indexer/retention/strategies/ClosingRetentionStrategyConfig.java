/*
 * */
package com.synectiks.process.server.indexer.retention.strategies;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.indexer.retention.RetentionStrategyConfig;

import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.Min;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class ClosingRetentionStrategyConfig implements RetentionStrategyConfig {
    private static final int DEFAULT_MAX_NUMBER_OF_INDICES = 20;

    @JsonProperty("max_number_of_indices")
    public abstract int maxNumberOfIndices();

    @JsonCreator
    public static ClosingRetentionStrategyConfig create(@JsonProperty(TYPE_FIELD) String type,
                                                        @JsonProperty("max_number_of_indices") @Min(1) int maxNumberOfIndices) {
        return new AutoValue_ClosingRetentionStrategyConfig(type, maxNumberOfIndices);
    }

    @JsonCreator
    public static ClosingRetentionStrategyConfig create(@JsonProperty("max_number_of_indices") @Min(1) int maxNumberOfIndices) {
        return new AutoValue_ClosingRetentionStrategyConfig(ClosingRetentionStrategyConfig.class.getCanonicalName(), maxNumberOfIndices);
    }

    public static ClosingRetentionStrategyConfig createDefault() {
        return create(DEFAULT_MAX_NUMBER_OF_INDICES);
    }
}
