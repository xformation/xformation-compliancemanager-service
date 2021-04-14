/*
 * */
package com.synectiks.process.common.security.shares;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.security.Capability;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@AutoValue
@JsonAutoDetect
public abstract class EntityShareRequest {

    public static final String SELECTED_GRANTEE_CAPABILITIES = "selected_grantee_capabilities";

    @JsonProperty(SELECTED_GRANTEE_CAPABILITIES)
    public abstract Optional<ImmutableMap<GRN, Capability>> selectedGranteeCapabilities();

    public Set<GRN> grantees() {
        return selectedGranteeCapabilities().map(ImmutableMap::keySet).orElse(ImmutableSet.of());
    }

    public Set<Capability> capabilities() {
        return selectedGranteeCapabilities()
                .map(ImmutableMap::values)
                .map(ImmutableSet::copyOf)
                .orElse(ImmutableSet.of());
    }

    @JsonCreator
    public static EntityShareRequest create(@JsonProperty("selected_grantee_capabilities") @Nullable Map<GRN, Capability> selectedGranteeCapabilities) {
        final ImmutableMap<GRN, Capability> value = selectedGranteeCapabilities == null ? null : ImmutableMap.copyOf(selectedGranteeCapabilities);
        return new AutoValue_EntityShareRequest(Optional.ofNullable(value));
    }
}
