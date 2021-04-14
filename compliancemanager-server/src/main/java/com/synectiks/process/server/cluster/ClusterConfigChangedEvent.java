/*
 * */
package com.synectiks.process.server.cluster;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;
import org.joda.time.DateTime;

import javax.validation.constraints.NotEmpty;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class ClusterConfigChangedEvent {
    @JsonProperty
    public abstract DateTime date();

    @JsonProperty
    @NotEmpty
    public abstract String nodeId();

    @JsonProperty
    @NotEmpty
    public abstract String type();

    @JsonCreator
    public static ClusterConfigChangedEvent create(@JsonProperty("date") DateTime date,
                                                   @JsonProperty("node_id") @NotEmpty String nodeId,
                                                   @JsonProperty("type") @NotEmpty String type) {
        return new AutoValue_ClusterConfigChangedEvent(date, nodeId, type);
    }
}
