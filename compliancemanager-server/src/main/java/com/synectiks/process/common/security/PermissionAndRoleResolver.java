/*
 * */
package com.synectiks.process.common.security;

import org.apache.shiro.authz.Permission;

import com.synectiks.process.common.grn.GRN;

import java.util.Set;

/**
 * Resolves a principal to specific permissions based on grants.
 */
public interface PermissionAndRoleResolver {
    /**
     * Returns resolved permissions for the given principal.
     *
     * @param principal the principal
     * @return the resolved permissions
     */
    Set<Permission> resolvePermissionsForPrincipal(GRN principal);

    /**
     * Returns roles for the given principal.
     *
     * @param principal the principal
     * @return the resolved roleIds
     */
    Set<String> resolveRolesForPrincipal(GRN principal);
}
