/*
 * */
package com.synectiks.process.common.plugins.sidecar.rest.responses;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.sidecar.rest.models.CollectorAction;
import com.synectiks.process.common.plugins.sidecar.rest.models.SidecarRegistrationConfiguration;
import com.synectiks.process.common.plugins.sidecar.rest.requests.ConfigurationAssignment;

import javax.annotation.Nullable;
import java.util.List;

@AutoValue
@JsonAutoDetect
public abstract class RegistrationResponse {
    @JsonProperty("configuration")
    public abstract SidecarRegistrationConfiguration configuration();

    @JsonProperty("configuration_override")
    public abstract boolean configurationOverride();

    @JsonProperty("actions")
    @Nullable
    public abstract List<CollectorAction> actions();

    @JsonProperty("assignments")
    @Nullable
    public abstract List<ConfigurationAssignment> assignments();

    @JsonCreator
    public static RegistrationResponse create(
            @JsonProperty("configuration") SidecarRegistrationConfiguration configuration,
            @JsonProperty("configuration_override") boolean configurationOverride,
            @JsonProperty("actions") @Nullable List<CollectorAction> actions,
            @JsonProperty("assignments") @Nullable List<ConfigurationAssignment> assignments) {
        return new AutoValue_RegistrationResponse(
                configuration,
                configurationOverride,
                actions,
                assignments);
    }
}
