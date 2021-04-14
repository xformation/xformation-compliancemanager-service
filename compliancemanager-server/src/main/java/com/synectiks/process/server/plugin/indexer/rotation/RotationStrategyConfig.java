/*
 * */
package com.synectiks.process.server.plugin.indexer.rotation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = RotationStrategyConfig.TYPE_FIELD, visible = true)
public interface RotationStrategyConfig {
    String TYPE_FIELD = "type";

    @JsonProperty(TYPE_FIELD)
    String type();
}
