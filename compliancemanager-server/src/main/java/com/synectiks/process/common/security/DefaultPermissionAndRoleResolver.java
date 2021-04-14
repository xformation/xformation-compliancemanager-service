/*
 * */
package com.synectiks.process.common.security;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.security.permissions.GRNPermission;
import com.synectiks.process.server.shared.security.RestPermissions;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class DefaultPermissionAndRoleResolver implements PermissionAndRoleResolver {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultPermissionAndRoleResolver.class);

    private final Logger logger;
    private final BuiltinCapabilities builtinCapabilities;
    private final DBGrantService grantService;

    @Inject
    public DefaultPermissionAndRoleResolver(BuiltinCapabilities builtinCapabilities,
                                            DBGrantService grantService) {
        this(LOG, builtinCapabilities, grantService);
    }

    public DefaultPermissionAndRoleResolver(Logger logger,
                                            BuiltinCapabilities builtinCapabilities,
                                            DBGrantService grantService) {
        this.logger = logger;
        this.builtinCapabilities = builtinCapabilities;
        this.grantService = grantService;
    }

    protected Set<GRN> resolveTargets(GRN target) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (target.type()) {
            case "system":
                // TODO
                return Collections.emptySet();
            default: // any other single entity
                return Collections.singleton(target);
        }
    }

    protected Set<GRN> resolveGrantees(GRN principal) {
        return Collections.singleton(principal);
    }

    @Override
    public Set<Permission> resolvePermissionsForPrincipal(GRN principal) {
        final Set<GrantDTO> grants = grantService.getForGranteesOrGlobal(resolveGrantees(principal));

        final ImmutableSet.Builder<Permission> permissionsBuilder = ImmutableSet.builder();

        for (GrantDTO grant : grants) {
            final Optional<CapabilityDescriptor> capability = builtinCapabilities.get(grant.capability());

            if (capability.isPresent()) {
                final Set<GRN> targets = resolveTargets(grant.target());

                for (String permission : capability.get().permissions()) {
                    for (GRN target : targets) {
                        if (target.isPermissionApplicable(permission)) {
                            // TODO Find a better way to distinguish between old and new types of permissions
                            // Possible solution: Don't use strings for the constants
                            if (permission.equals(RestPermissions.ENTITY_OWN)) {
                                permissionsBuilder.add(GRNPermission.create(permission, target));
                            } else {
                                permissionsBuilder.add(new WildcardPermission(permission + ":" + target.entity()));
                            }
                        }
                    }
                }
            } else {
                logger.warn("Couldn't find capability <{}>", grant.capability());
            }
        }

        return permissionsBuilder.build();
    }

    @Override
    public Set<String> resolveRolesForPrincipal(GRN principal) {
        return ImmutableSet.of();
    }
}
