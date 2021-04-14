/*
 * */
package com.synectiks.process.common.security.authservice;

import com.google.auto.value.AutoValue;
import com.synectiks.process.server.security.encryption.EncryptedValue;

@AutoValue
public abstract class AuthServiceCredentials {
    public abstract String username();

    public abstract EncryptedValue password();

    /**
     * Returns true if the subject is already authenticated and the authentication service backend doesn't need
     * to authenticate anymore.
     *
     * @return true if already authenticated, false otherwise
     */
    public abstract boolean isAuthenticated();

    public static AuthServiceCredentials create(String username, EncryptedValue password) {
        return builder()
                .username(username)
                .password(password)
                .isAuthenticated(false)
                .build();
    }

    public static AuthServiceCredentials createAuthenticated(String username) {
        return builder()
                .username(username)
                .password(EncryptedValue.createUnset())
                .isAuthenticated(true)
                .build();
    }

    public static Builder builder() {
        return Builder.create();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public static Builder create() {
            return new AutoValue_AuthServiceCredentials.Builder()
                    .isAuthenticated(false);
        }

        public abstract Builder username(String username);

        public abstract Builder password(EncryptedValue password);

        public abstract Builder isAuthenticated(boolean isAuthenticated);

        public abstract AuthServiceCredentials build();
    }
}
