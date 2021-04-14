/*
 * */
package com.synectiks.process.server.plugin.security;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.security.authzroles.BuiltinRole;

import java.util.Set;

public interface PluginPermissions {
    Set<Permission> permissions();

    Set<Permission> readerBasePermissions();

    /**
     * A set of built-in roles that should be added to every alertmanager setup.
     * @return The roles that this plugin provides
     */
    default Set<BuiltinRole> builtinRoles() {
        return ImmutableSet.of();
    }
}
