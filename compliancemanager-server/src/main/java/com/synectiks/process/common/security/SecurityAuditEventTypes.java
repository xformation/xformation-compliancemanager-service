/*
 * */
package com.synectiks.process.common.security;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.audit.PluginAuditEventTypes;

import java.util.Set;

public class SecurityAuditEventTypes implements PluginAuditEventTypes {
    public static final String NAMESPACE = "security:";
    public static final String NAMESPACE_AUTH_SERVICE_GLOBAL_CONFIG = NAMESPACE + "auth_service_global_config:";
    public static final String NAMESPACE_AUTH_SERVICE_BACKEND = NAMESPACE + "auth_service_backend:";

    public static final String SHARE_CREATE = NAMESPACE + "share:create";
    public static final String SHARE_UPDATE = NAMESPACE + "share:update";
    public static final String SHARE_DELETE = NAMESPACE + "share:delete";

    public static final String AUTH_SERVICE_GLOBAL_CONFIG_UPDATE = NAMESPACE_AUTH_SERVICE_GLOBAL_CONFIG + "update";
    public static final String AUTH_SERVICE_BACKEND_CREATE = NAMESPACE_AUTH_SERVICE_BACKEND + "create";
    public static final String AUTH_SERVICE_BACKEND_DELETE = NAMESPACE_AUTH_SERVICE_BACKEND + "delete";
    public static final String AUTH_SERVICE_BACKEND_UPDATE = NAMESPACE_AUTH_SERVICE_BACKEND + "update";

    private static final ImmutableSet<String> EVENT_TYPES = ImmutableSet.of(
            SHARE_CREATE,
            SHARE_UPDATE,
            SHARE_DELETE,
            AUTH_SERVICE_GLOBAL_CONFIG_UPDATE,
            AUTH_SERVICE_BACKEND_CREATE,
            AUTH_SERVICE_BACKEND_DELETE,
            AUTH_SERVICE_BACKEND_UPDATE
    );

    @Override
    public Set<String> auditEventTypes() {
        return EVENT_TYPES;
    }
}

