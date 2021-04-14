/*
 * */
package com.synectiks.process.common.plugins.sidecar.rest.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.sidecar.rest.models.NodeDetails;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AutoValue
@JsonAutoDetect
public abstract class RegistrationRequest {
    @JsonProperty("node_name")
    @NotNull
    @Size(min = 1)
    public abstract String nodeName();

    @JsonProperty("node_details")
    public abstract NodeDetails nodeDetails();

    @JsonCreator
    public static RegistrationRequest create(@JsonProperty("node_name") String nodeName,
                                             @JsonProperty("node_details") @Valid NodeDetails nodeDetails) {
        return new AutoValue_RegistrationRequest(nodeName, nodeDetails);
    }
}
