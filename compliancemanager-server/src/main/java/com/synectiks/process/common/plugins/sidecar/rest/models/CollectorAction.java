/*
 * */
package com.synectiks.process.common.plugins.sidecar.rest.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

@AutoValue
@JsonAutoDetect
public abstract class CollectorAction {

    @JsonProperty("collector_id")
    public abstract String collectorId();

    @JsonProperty("properties")
    public abstract Map<String, Object> properties();

    @JsonCreator
    public static CollectorAction create(@JsonProperty("collector_id") String collectorId,
                                         @JsonProperty("properties") Map<String, Object> properties) {
        return new AutoValue_CollectorAction(collectorId, properties);
    }

    public static CollectorAction create(String collectorId, String action) {
        return create(collectorId, ImmutableMap.of(action, true));
    }
}
