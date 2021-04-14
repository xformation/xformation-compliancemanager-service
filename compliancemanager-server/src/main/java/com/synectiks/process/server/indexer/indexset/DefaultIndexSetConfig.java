/*
 * */
package com.synectiks.process.server.indexer.indexset;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonAutoDetect
public abstract class DefaultIndexSetConfig {
    @JsonProperty("default_index_set_id")
    public abstract String defaultIndexSetId();

    @JsonCreator
    public static DefaultIndexSetConfig create(@JsonProperty("default_index_set_id") String defaultIndexSetId) {
        return new AutoValue_DefaultIndexSetConfig(defaultIndexSetId);
    }
}
