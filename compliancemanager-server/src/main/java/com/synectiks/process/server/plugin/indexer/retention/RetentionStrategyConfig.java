/*
 * */
package com.synectiks.process.server.plugin.indexer.retention;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = RetentionStrategyConfig.TYPE_FIELD, visible = true)
public interface RetentionStrategyConfig {
    String TYPE_FIELD = "type";

    @JsonProperty(TYPE_FIELD)
    String type();
}
