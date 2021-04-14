/*
 * */
package com.synectiks.process.server.indexer.retention.strategies;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.indexer.retention.RetentionStrategyConfig;

import org.graylog.autovalue.WithBeanGetter;

/**
 * This is being used as the fallback {@link RetentionStrategyConfig} if the requested class is not
 * available (usually because it was contributed by a plugin which is not loaded).
 * <p>
 * By itself it does nothing useful except accepting all properties but not exposing them.
 */
@JsonAutoDetect
@AutoValue
@WithBeanGetter
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class UnknownRetentionStrategyConfig implements RetentionStrategyConfig {

    @JsonCreator
    public static UnknownRetentionStrategyConfig create() {
        return new AutoValue_UnknownRetentionStrategyConfig(UnknownRetentionStrategyConfig.class.getCanonicalName());
    }


    public static UnknownRetentionStrategyConfig createDefault() {
        return create();
    }
}
