/*
 * */
package com.synectiks.process.server.rest.models.users.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.security.permissions.GRNPermission;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.rest.models.users.requests.Startpage;

import org.apache.shiro.authz.permission.WildcardPermission;
import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class UserSummary {

    @JsonProperty
    @Nullable
    public abstract String id();

    @JsonProperty
    public abstract String username();

    @JsonProperty
    public abstract String email();

    @JsonProperty
    public abstract String fullName();

    @JsonProperty
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    public abstract List<WildcardPermission> permissions();

    @JsonProperty
    public abstract List<GRNPermission> grnPermissions();

    @JsonProperty
    @Nullable
    public abstract Map<String, Object> preferences();

    @JsonProperty
    @Nullable
    public abstract String timezone();

    @JsonProperty
    @Nullable
    public abstract Long sessionTimeoutMs();

    @JsonProperty("read_only")
    public abstract boolean readOnly();

    @JsonProperty
    public abstract boolean external();

    @JsonProperty
    @Nullable
    public abstract Startpage startpage();

    @JsonProperty
    @Nullable
    public abstract Set<String> roles();

    @JsonProperty("session_active")
    public abstract boolean sessionActive();

    @JsonProperty("last_activity")
    @Nullable
    public abstract Date lastActivity();

    @JsonProperty("client_address")
    @Nullable
    public abstract String clientAddress();

    @JsonProperty("account_status")
    public abstract User.AccountStatus accountStatus();

    @JsonCreator
    public static UserSummary create(@JsonProperty("id") @Nullable String id,
                                     @JsonProperty("username") String username,
                                     @JsonProperty("email") String email,
                                     @JsonProperty("full_name") @Nullable String fullName,
                                     @JsonProperty("permissions") @Nullable List<WildcardPermission> permissions,
                                     @JsonProperty("grn_permissions") @Nullable List<GRNPermission> grnPermissions,
                                     @JsonProperty("preferences") @Nullable Map<String, Object> preferences,
                                     @JsonProperty("timezone") @Nullable String timezone,
                                     @JsonProperty("session_timeout_ms") @Nullable Long sessionTimeoutMs,
                                     @JsonProperty("read_only") boolean readOnly,
                                     @JsonProperty("external") boolean external,
                                     @JsonProperty("startpage") @Nullable Startpage startpage,
                                     @JsonProperty("roles") @Nullable Set<String> roles,
                                     @JsonProperty("session_active") boolean sessionActive,
                                     @JsonProperty("last_activity") @Nullable Date lastActivity,
                                     @JsonProperty("client_address") @Nullable String clientAddress,
                                     @JsonProperty("account_status") User.AccountStatus accountStatus) {
        return new AutoValue_UserSummary(id,
                                         username,
                                         email,
                                         fullName,
                                         permissions,
                                         grnPermissions,
                                         preferences,
                                         timezone,
                                         sessionTimeoutMs,
                                         readOnly,
                                         external,
                                         startpage,
                                         roles,
                                         sessionActive,
                                         lastActivity,
                                         clientAddress,
                                         accountStatus);
    }
}
