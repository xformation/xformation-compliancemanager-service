/*
 * */
package com.synectiks.process.common.security.authservice.backend;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.security.authservice.AuthServiceBackendConfig;
import com.synectiks.process.common.security.authservice.ldap.LDAPConnectorConfig;
import com.synectiks.process.common.security.authservice.ldap.LDAPConnectorConfigProvider;
import com.synectiks.process.common.security.authservice.ldap.LDAPTransportSecurity;
import com.synectiks.process.server.plugin.rest.ValidationResult;
import com.synectiks.process.server.security.encryption.EncryptedValue;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

@AutoValue
@JsonDeserialize(builder = LDAPAuthServiceBackendConfig.Builder.class)
@JsonTypeName(LDAPAuthServiceBackend.TYPE_NAME)
public abstract class LDAPAuthServiceBackendConfig implements AuthServiceBackendConfig, LDAPConnectorConfigProvider {
    private static final String FIELD_SERVERS = "servers";
    private static final String FIELD_TRANSPORT_SECURITY = "transport_security";
    private static final String FIELD_VERIFY_CERTIFICATES = "verify_certificates";
    private static final String FIELD_SYSTEM_USER_DN = "system_user_dn";
    private static final String FIELD_SYSTEM_USER_PASSWORD = "system_user_password";
    private static final String FIELD_USER_SEARCH_BASE = "user_search_base";
    private static final String FIELD_USER_SEARCH_PATTERN = "user_search_pattern";
    private static final String FIELD_USER_UNIQUE_ID_ATTRIBUTE = "user_unique_id_attribute";
    private static final String FIELD_USER_NAME_ATTRIBUTE = "user_name_attribute";
    private static final String FIELD_USER_FULL_NAME_ATTRIBUTE = "user_full_name_attribute";

    @JsonProperty(FIELD_SERVERS)
    public abstract ImmutableList<HostAndPort> servers();

    @JsonProperty(FIELD_TRANSPORT_SECURITY)
    public abstract LDAPTransportSecurity transportSecurity();

    @JsonProperty(FIELD_VERIFY_CERTIFICATES)
    public abstract boolean verifyCertificates();

    @JsonProperty(FIELD_SYSTEM_USER_DN)
    public abstract String systemUserDn();

    @JsonProperty(FIELD_SYSTEM_USER_PASSWORD)
    public abstract EncryptedValue systemUserPassword();

    @JsonProperty(FIELD_USER_SEARCH_BASE)
    public abstract String userSearchBase();

    @JsonProperty(FIELD_USER_SEARCH_PATTERN)
    public abstract String userSearchPattern();

    @JsonProperty(FIELD_USER_UNIQUE_ID_ATTRIBUTE)
    public abstract String userUniqueIdAttribute();

    @JsonProperty(FIELD_USER_NAME_ATTRIBUTE)
    public abstract String userNameAttribute();

    @JsonProperty(FIELD_USER_FULL_NAME_ATTRIBUTE)
    public abstract String userFullNameAttribute();

    @Override
    public void validate(ValidationResult result) {
        if (servers().isEmpty()) {
            result.addError(FIELD_SERVERS, "Server list cannot be empty.");
        }
        if (isBlank(userSearchBase())) {
            result.addError(FIELD_USER_SEARCH_BASE, "User search base cannot be empty.");
        }
        if (isBlank(userSearchPattern())) {
            result.addError(FIELD_USER_SEARCH_PATTERN, "User search pattern cannot be empty.");
        } else {
            try {
                Filter.create(userSearchPattern());
            } catch (LDAPException e) {
                result.addError(FIELD_USER_SEARCH_PATTERN, "User search pattern cannot be parsed. It must be a valid LDAP filter.");
            }
        }
        if (isBlank(userUniqueIdAttribute())) {
            result.addError(FIELD_USER_UNIQUE_ID_ATTRIBUTE, "User unique ID attribute cannot be empty.");
        }
        if (isBlank(userNameAttribute())) {
            result.addError(FIELD_USER_NAME_ATTRIBUTE, "User name attribute cannot be empty.");
        }
        if (isBlank(userFullNameAttribute())) {
            result.addError(FIELD_USER_FULL_NAME_ATTRIBUTE, "User full name cannot be empty.");
        }
    }

    @Override
    public LDAPConnectorConfig getLDAPConnectorConfig() {
        return LDAPConnectorConfig.builder()
                .serverList(servers().stream()
                        .map(hap -> LDAPConnectorConfig.LDAPServer.create(hap.host(), hap.port()))
                        .collect(Collectors.toList()))
                .systemUsername(StringUtils.trimToNull(systemUserDn()))
                .systemPassword(systemUserPassword())
                .transportSecurity(transportSecurity())
                .verifyCertificates(verifyCertificates())
                .build();
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return Builder.create();
    }

    @AutoValue.Builder
    public abstract static class Builder implements AuthServiceBackendConfig.Builder<Builder> {
        @JsonCreator
        public static Builder create() {
            return new AutoValue_LDAPAuthServiceBackendConfig.Builder()
                    .type(LDAPAuthServiceBackend.TYPE_NAME)
                    .verifyCertificates(true)
                    .systemUserDn("")
                    .systemUserPassword(EncryptedValue.createUnset())
                    .userUniqueIdAttribute("entryUUID");
        }

        @JsonProperty(FIELD_SERVERS)
        public abstract Builder servers(List<HostAndPort> servers);

        @JsonProperty(FIELD_TRANSPORT_SECURITY)
        public abstract Builder transportSecurity(LDAPTransportSecurity transportSecurity);

        @JsonProperty(FIELD_VERIFY_CERTIFICATES)
        public abstract Builder verifyCertificates(boolean verifyCertificates);

        @JsonProperty(FIELD_SYSTEM_USER_DN)
        public abstract Builder systemUserDn(String systemUserDn);

        @JsonProperty(FIELD_SYSTEM_USER_PASSWORD)
        public abstract Builder systemUserPassword(EncryptedValue systemUserPassword);

        @JsonProperty(FIELD_USER_SEARCH_BASE)
        public abstract Builder userSearchBase(String userSearchBase);

        @JsonProperty(FIELD_USER_SEARCH_PATTERN)
        public abstract Builder userSearchPattern(String userSearchPattern);

        @JsonProperty(FIELD_USER_UNIQUE_ID_ATTRIBUTE)
        public abstract Builder userUniqueIdAttribute(String userUniqueIdAttribute);

        @JsonProperty(FIELD_USER_NAME_ATTRIBUTE)
        public abstract Builder userNameAttribute(String userNameAttribute);

        @JsonProperty(FIELD_USER_FULL_NAME_ATTRIBUTE)
        public abstract Builder userFullNameAttribute(String userFullNameAttribute);

        public abstract LDAPAuthServiceBackendConfig build();
    }

    @AutoValue
    public static abstract class HostAndPort {
        @JsonProperty("host")
        public abstract String host();

        @JsonProperty("port")
        public abstract int port();

        @JsonCreator
        public static HostAndPort create(@JsonProperty("host") String host, @JsonProperty("port") int port) {
            return new AutoValue_LDAPAuthServiceBackendConfig_HostAndPort(host, port);
        }

        @Override
        public String toString() {
            return host() + ":" + port();
        }
    }
}
