/*
 * */
package com.synectiks.process.server.rest.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class HelloWorldResponse {
    @JsonProperty("cluster_id")
    public abstract String clusterId();

    @JsonProperty("node_id")
    public abstract String nodeId();

    @JsonProperty("version")
    public abstract String version();

    @JsonProperty("tagline")
    public abstract String tagline();

    public static HelloWorldResponse create(String clusterId, String nodeId, String version, String tagline) {
        return new AutoValue_HelloWorldResponse(clusterId, nodeId, version, tagline);
    }
}
