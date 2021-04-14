/*
 * */
package com.synectiks.process.server.rest.models.system.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import javax.annotation.Nullable;

@JsonAutoDetect
public abstract class IOStateSummary {
    @JsonProperty
    public abstract String id();
    @JsonProperty
    public abstract String state();
    @JsonProperty
    public abstract DateTime startedAt();
    @JsonProperty
    @Nullable
    public abstract String detailedMessage();
}
