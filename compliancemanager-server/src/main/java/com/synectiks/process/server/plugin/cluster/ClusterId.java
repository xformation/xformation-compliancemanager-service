/*
 * */
package com.synectiks.process.server.plugin.cluster;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class ClusterId {
    @JsonProperty
    public abstract String clusterId();

    @JsonCreator
    public static ClusterId create(@JsonProperty("cluster_id") String clusterId) {
        return new AutoValue_ClusterId(clusterId);
    }
}
