/*
 * */
package com.synectiks.process.server.indexer.rotation.strategies;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.joschi.jadconfig.util.Size;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategyConfig;

import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.Min;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class SizeBasedRotationStrategyConfig implements RotationStrategyConfig {
    private static final long DEFAULT_MAX_SIZE = Size.gigabytes(1L).toBytes();

    @JsonProperty("max_size")
    public abstract long maxSize();

    @JsonCreator
    public static SizeBasedRotationStrategyConfig create(@JsonProperty(TYPE_FIELD) String type,
                                                         @JsonProperty("max_size") @Min(1) long maxSize) {
        return new AutoValue_SizeBasedRotationStrategyConfig(type, maxSize);
    }

    @JsonCreator
    public static SizeBasedRotationStrategyConfig create(@JsonProperty("max_size") @Min(1) long maxSize) {
        return create(SizeBasedRotationStrategyConfig.class.getCanonicalName(), maxSize);
    }

    public static SizeBasedRotationStrategyConfig createDefault() {
        return create(DEFAULT_MAX_SIZE);
    }
}
