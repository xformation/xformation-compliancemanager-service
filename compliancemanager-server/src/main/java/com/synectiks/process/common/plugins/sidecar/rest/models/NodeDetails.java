/*
 * */
package com.synectiks.process.common.plugins.sidecar.rest.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@AutoValue
@JsonAutoDetect
public abstract class NodeDetails {
    @JsonProperty("operating_system")
    @NotNull
    @Size(min = 1)
    public abstract String operatingSystem();

    @JsonProperty("ip")
    @Nullable
    public abstract String ip();

    @JsonProperty("metrics")
    @Nullable
    public abstract NodeMetrics metrics();

    @JsonProperty("log_file_list")
    @Nullable
    public abstract List<NodeLogFile> logFileList();

    @JsonProperty("status")
    @Nullable
    public abstract CollectorStatusList statusList();

    @JsonCreator
    public static NodeDetails create(@JsonProperty("operating_system") String operatingSystem,
                                     @JsonProperty("ip") @Nullable String ip,
                                     @JsonProperty("metrics") @Nullable NodeMetrics metrics,
                                     @JsonProperty("log_file_list") @Nullable List<NodeLogFile> logFileList,
                                     @JsonProperty("status") @Nullable CollectorStatusList statusList) {
        return new AutoValue_NodeDetails(operatingSystem, ip, metrics, logFileList, statusList);
    }
}
