/*
 * */
package com.synectiks.process.common.security.authservice;

import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

@AutoValue
public abstract class AuthServiceResult {
    public abstract String username();

    @Nullable
    public abstract String userProfileId();

    public abstract String backendId();

    public abstract String backendType();

    public abstract String backendTitle();

    public abstract Map<String, Object> sessionAttributes();

    public boolean isSuccess() {
        return !isNullOrEmpty(userProfileId());
    }

    public static Builder builder() {
        return Builder.create();
    }

    public static AuthServiceResult failed(String username, AuthServiceBackend backend) {
        return builder()
                .username(username)
                .backendId(backend.backendId())
                .backendType(backend.backendType())
                .backendTitle(backend.backendTitle())
                .build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public static Builder create() {
            return new AutoValue_AuthServiceResult.Builder().sessionAttributes(Collections.emptyMap());
        }

        public abstract Builder username(String username);

        public abstract Builder userProfileId(String userProfileId);

        public abstract Builder backendId(String backendId);

        public abstract Builder backendType(String backendType);

        public abstract Builder backendTitle(String backendTitle);

        public abstract Builder sessionAttributes(Map<String, Object> sessionAttributes);

        public abstract AuthServiceResult build();
    }
}
