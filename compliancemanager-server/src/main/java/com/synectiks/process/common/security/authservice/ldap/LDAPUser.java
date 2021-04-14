/*
 * */
package com.synectiks.process.common.security.authservice.ldap;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class LDAPUser {
    public abstract String base64UniqueId();

    public abstract boolean accountIsEnabled();

    public abstract String username();

    public abstract String fullName();

    public abstract String email();

    public abstract LDAPEntry entry();

    public String dn() {
        return entry().dn();
    }

    public static Builder builder() {
        return Builder.create();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public static Builder create() {
            return new AutoValue_LDAPUser.Builder();
        }

        public abstract Builder base64UniqueId(String base64UniqueId);

        public abstract Builder accountIsEnabled(boolean isEnabled);

        public abstract Builder username(String username);

        public abstract Builder fullName(String fullName);

        public abstract Builder email(String email);

        public abstract Builder entry(LDAPEntry entry);

        public abstract LDAPUser build();
    }
}
