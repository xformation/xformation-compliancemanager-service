/*
 * */
package com.synectiks.process.server.security.headerauth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import javax.validation.constraints.NotBlank;

@AutoValue
@JsonDeserialize(builder = HTTPHeaderAuthConfig.Builder.class)
public abstract class HTTPHeaderAuthConfig {
    private static final String FIELD_ENABLED = "enabled";
    private static final String FIELD_USERNAME_HEADER = "username_header";

    private static final String DEFAULT_USERNAME_HEADER = "Remote-User";

    @JsonProperty(FIELD_ENABLED)
    public abstract boolean enabled();

    @JsonProperty(FIELD_USERNAME_HEADER)
    @NotBlank
    public abstract String usernameHeader();

    public static HTTPHeaderAuthConfig createDisabled() {
        return builder().enabled(false).build();
    }

    public static Builder builder() {
        return Builder.create();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonCreator
        public static Builder create() {
            return new AutoValue_HTTPHeaderAuthConfig.Builder()
                    .enabled(false)
                    .usernameHeader(DEFAULT_USERNAME_HEADER);
        }

        @JsonProperty(FIELD_ENABLED)
        public abstract Builder enabled(boolean enabled);

        @JsonProperty(FIELD_USERNAME_HEADER)
        public abstract Builder usernameHeader(String usernameHeader);

        public abstract HTTPHeaderAuthConfig build();
    }
}
