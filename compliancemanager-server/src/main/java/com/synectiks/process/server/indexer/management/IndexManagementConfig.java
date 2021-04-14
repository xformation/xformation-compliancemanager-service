/*
 * */
package com.synectiks.process.server.indexer.management;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class IndexManagementConfig {
    @JsonProperty("rotation_strategy")
    public abstract String rotationStrategy();

    @JsonProperty("retention_strategy")
    public abstract String retentionStrategy();

    @JsonCreator
    public static IndexManagementConfig create(@JsonProperty("rotation_strategy") String rotationStrategy,
                                               @JsonProperty("retention_strategy") String retentionStrategy) {
        return new AutoValue_IndexManagementConfig(rotationStrategy, retentionStrategy);
    }
}
