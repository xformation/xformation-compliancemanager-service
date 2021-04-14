/*
 * */
package com.synectiks.process.common.plugins.sidecar.rest.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

@AutoValue
@JsonAutoDetect
public abstract class CollectorStatusList {
    @JsonProperty("status")
    public abstract int status();

    @JsonProperty("message")
    public abstract String message();

    @JsonProperty("collectors")
    public abstract ImmutableSet<CollectorStatus> collectors();

    @JsonCreator
    public static CollectorStatusList create(@JsonProperty("status") int status,
                                             @JsonProperty("message") String message,
                                             @JsonProperty("collectors") Set<CollectorStatus> collectors) {
        return new AutoValue_CollectorStatusList(status, message, ImmutableSet.copyOf(collectors));
    }}
