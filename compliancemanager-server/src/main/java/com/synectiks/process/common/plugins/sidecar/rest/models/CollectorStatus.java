/*
 * */
package com.synectiks.process.common.plugins.sidecar.rest.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonAutoDetect
public abstract class CollectorStatus {
    @JsonProperty("collector_id")
    public abstract String collectorId();

    @JsonProperty("status")
    public abstract int status();

    @JsonProperty("message")
    public abstract String message();

    @JsonProperty("verbose_message")
    public abstract String verboseMessage();

    @JsonCreator
    public static CollectorStatus create(@JsonProperty("collector_id") String collectorId,
                                         @JsonProperty("status") int status,
                                         @JsonProperty("message") String message,
                                         @JsonProperty("verbose_message") String verboseMessage) {
        return new AutoValue_CollectorStatus(collectorId, status, message, verboseMessage);
    }
}