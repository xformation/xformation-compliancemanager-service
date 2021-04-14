/*
 * */
package com.synectiks.process.server.rest.models.system.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class SystemOverviewResponse {
    @JsonProperty
    public abstract String facility();
    @JsonProperty
    public abstract String codename();
    @JsonProperty
    public abstract String nodeId();
    @JsonProperty
    public abstract String clusterId();
    @JsonProperty
    public abstract String version();
    @JsonProperty
    public abstract String startedAt();
    @JsonProperty("is_processing")
    public abstract boolean isProcessing();
    @JsonProperty
    public abstract String hostname();
    @JsonProperty
    public abstract String lifecycle();
    @JsonProperty
    public abstract String lbStatus();
    @JsonProperty
    public abstract String timezone();
    @JsonProperty("operating_system")
    public abstract String operatingSystem();

    @JsonCreator
    public static SystemOverviewResponse create(@JsonProperty("facility") String facility,
                                                @JsonProperty("codename") String codename,
                                                @JsonProperty("node_id") String nodeId,
                                                @JsonProperty("cluster_id") String clusterId,
                                                @JsonProperty("version") String version,
                                                @JsonProperty("started_at") String startedAt,
                                                @JsonProperty("is_processing") boolean isProcessing,
                                                @JsonProperty("hostname") String hostname,
                                                @JsonProperty("lifecycle") String lifecycle,
                                                @JsonProperty("lb_status") String lbStatis,
                                                @JsonProperty("timezone") String timezone,
                                                @JsonProperty("operating_system") String operatingSystem) {
        return new AutoValue_SystemOverviewResponse(facility, codename, nodeId, clusterId, version, startedAt, isProcessing, hostname, lifecycle, lbStatis, timezone, operatingSystem);
    }
}
