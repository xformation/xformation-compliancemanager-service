/*
 * */
package com.synectiks.process.common.security;

import com.google.auto.value.AutoValue;

import java.util.Set;

@AutoValue
public abstract class CapabilityDescriptor {
    public abstract Capability capability();

    public abstract String title();

    public abstract Set<String> permissions();

    public static CapabilityDescriptor create(Capability capability, String title, Set<String> permissions) {
        return builder()
                .capability(capability)
                .title(title)
                .permissions(permissions)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_CapabilityDescriptor.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder capability(Capability capability);

        public abstract Builder title(String title);

        public abstract Builder permissions(Set<String> permissions);

        public abstract CapabilityDescriptor build();
    }
}
