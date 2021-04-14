/*
 * */
package com.synectiks.process.server.rest.models.system.indices;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.indexer.retention.RetentionStrategyConfig;

import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class RetentionStrategyDescription {
    @JsonProperty("type")
    public abstract String type();

    @JsonProperty("default_config")
    public abstract RetentionStrategyConfig defaultConfig();

    @JsonProperty("json_schema")
    public abstract JsonSchema jsonSchema();

    @JsonCreator
    public static RetentionStrategyDescription create(@JsonProperty("type") String type,
                                                      @JsonProperty("default_config") RetentionStrategyConfig defaultConfig,
                                                      @JsonProperty("json_schema") JsonSchema jsonSchema) {
        return new AutoValue_RetentionStrategyDescription(type, defaultConfig, jsonSchema);
    }
}
