/*
 * */
package com.synectiks.process.common.security.authservice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synectiks.process.server.plugin.rest.ValidationResult;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = AuthServiceBackendConfig.TYPE_FIELD,
        visible = true,
        defaultImpl = AuthServiceBackendConfig.FallbackConfig.class)
public interface AuthServiceBackendConfig {
    String TYPE_FIELD = "type";

    @JsonProperty(TYPE_FIELD)
    String type();

    @JsonIgnore
    default void validate(ValidationResult result) {
    }

    interface Builder<SELF> {
        @JsonProperty(TYPE_FIELD)
        SELF type(String type);
    }

    class FallbackConfig implements AuthServiceBackendConfig {
        @Override
        public String type() {
            throw new UnsupportedOperationException();
        }
    }
}
