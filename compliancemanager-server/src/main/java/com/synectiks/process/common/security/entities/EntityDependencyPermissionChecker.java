/*
 * */
package com.synectiks.process.common.security.entities;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.security.BuiltinCapabilities;
import com.synectiks.process.common.security.Capability;
import com.synectiks.process.common.security.CapabilityDescriptor;
import com.synectiks.process.common.security.GranteeAuthorizer;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class EntityDependencyPermissionChecker {
    private final GranteeAuthorizer.Factory granteeAuthorizerFactory;
    private final BuiltinCapabilities builtinCapabilities;

    @Inject
    public EntityDependencyPermissionChecker(GranteeAuthorizer.Factory granteeAuthorizerFactory,
                                             BuiltinCapabilities builtinCapabilities) {
        this.granteeAuthorizerFactory = granteeAuthorizerFactory;
        this.builtinCapabilities = builtinCapabilities;
    }

    /**
     * Runs permission checks for the given dependencies for every selected grantee and returns the entities that
     * grantees cannot access.
     *
     * @param sharingUser      the sharing user
     * @param dependencies     the dependencies to check
     * @param selectedGrantees the selected grantees
     * @return dependencies that grantees cannot access, grouped by grantee
     */
    public ImmutableMultimap<GRN, EntityDescriptor> check(GRN sharingUser,
                                                          ImmutableSet<EntityDescriptor> dependencies,
                                                          Set<GRN> selectedGrantees) {
        final ImmutableMultimap.Builder<GRN, EntityDescriptor> deniedDependencies = ImmutableMultimap.builder();
        final GranteeAuthorizer sharerAuthorizer = granteeAuthorizerFactory.create(sharingUser);

        for (final GRN grantee : selectedGrantees) {
            // We only check for existing grants for the actual grantee. If the grantee is a team, we only check if
            // the team has a grant, not if any users in the team can access the dependency via other grants.
            // The same for the "everyone" grantee, we only check if  the "everyone" grantee has access to a dependency.
            final GranteeAuthorizer granteeAuthorizer = granteeAuthorizerFactory.create(grantee);

            for (final EntityDescriptor dependency : dependencies) {
                // We can only expose missing dependencies that the sharing user can read to avoid
                // leaking information to the sharing user.
                if (cannotView(sharerAuthorizer, dependency)) {
                    continue;
                }

                if (cannotView(granteeAuthorizer, dependency)) {
                    deniedDependencies.put(grantee, dependency);
                }
            }
        }

        return deniedDependencies.build();
    }

    private boolean cannotView(GranteeAuthorizer authorizer, EntityDescriptor dependency) {
        final Optional<CapabilityDescriptor> capabilityDescriptor = builtinCapabilities.get(Capability.VIEW);

        return capabilityDescriptor.map(CapabilityDescriptor::permissions)
                .orElse(Collections.emptySet())
                .stream()
                .filter(permission -> dependency.id().isPermissionApplicable(permission))
                .noneMatch(permission -> authorizer.isPermitted(permission, dependency.id()));
    }
}
