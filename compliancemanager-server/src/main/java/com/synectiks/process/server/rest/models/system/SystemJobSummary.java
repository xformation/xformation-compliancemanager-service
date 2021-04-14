/*
 * */
package com.synectiks.process.server.rest.models.system;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.util.UUID;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class SystemJobSummary {
    @JsonProperty
    public abstract UUID id();

    @JsonProperty
    public abstract String description();

    @JsonProperty
    public abstract String name();

    @JsonProperty
    public abstract String info();

    @JsonProperty("node_id")
    public abstract String nodeId();

    @JsonProperty("started_at")
    @Nullable
    public abstract DateTime startedAt();

    @JsonProperty("percent_complete")
    public abstract int percentComplete();

    @JsonProperty("is_cancelable")
    public abstract boolean isCancelable();

    @JsonProperty("provides_progress")
    public abstract boolean providesProgress();

    @JsonCreator
    public static SystemJobSummary create(@JsonProperty("id") UUID id,
                                          @JsonProperty("description") String description,
                                          @JsonProperty("name") String name,
                                          @JsonProperty("info") String info,
                                          @JsonProperty("node_id") String nodeId,
                                          @JsonProperty("started_at") @Nullable DateTime startedAt,
                                          @JsonProperty("percent_complete") int percentComplete,
                                          @JsonProperty("is_cancelable") boolean isCancelable,
                                          @JsonProperty("provides_progress") boolean providesProgress) {
        return new AutoValue_SystemJobSummary(id, description, name, info, nodeId, startedAt,
                percentComplete, isCancelable, providesProgress);
    }
}
