/*
 * */
package com.synectiks.process.common.security.authservice.ldap;


import com.google.auto.value.AutoValue;

@AutoValue
public abstract class UnboundLDAPConfig {
    public abstract String userSearchBase();

    public abstract String userSearchPattern();

    public abstract String userUniqueIdAttribute();

    public abstract String userNameAttribute();

    public abstract String userFullNameAttribute();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return Builder.create();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public static Builder create() {
            return new AutoValue_UnboundLDAPConfig.Builder();
        }

        public abstract Builder userSearchBase(String userSearchBase);

        public abstract Builder userSearchPattern(String userSearchPattern);

        public abstract Builder userUniqueIdAttribute(String userUniqueIdAttribute);

        public abstract Builder userNameAttribute(String userNameAttribute);

        public abstract Builder userFullNameAttribute(String userFullNameAttribute);

        public abstract UnboundLDAPConfig build();
    }
}
