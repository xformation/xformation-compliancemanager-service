/*
 * */
package com.synectiks.process.common.security.shares;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNRegistry;
import com.synectiks.process.common.security.GranteeAuthorizer;
import com.synectiks.process.common.security.shares.EntityShareResponse.AvailableGrantee;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.shared.security.RestPermissions;
import com.synectiks.process.server.shared.users.UserService;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Set;

public class DefaultGranteeService implements GranteeService {
    protected final UserService userService;
    protected final GRNRegistry grnRegistry;
    protected final GranteeAuthorizer.Factory granteeAuthorizerFactory;

    @Inject
    public DefaultGranteeService(UserService userService, GRNRegistry grnRegistry, GranteeAuthorizer.Factory granteeAuthorizerFactory) {
        this.userService = userService;
        this.grnRegistry = grnRegistry;
        this.granteeAuthorizerFactory = granteeAuthorizerFactory;
    }

    @Override
    public ImmutableSet<AvailableGrantee> getAvailableGrantees(User sharingUser) {
        return ImmutableSet.<AvailableGrantee>builder()
                .addAll(getAvailableUserGrantees(sharingUser))
                .add(getGlobalGrantee())
                .build();
    }

    @Override
    public Set<GRN> getGranteeAliases(GRN grantee) {
        return Collections.singleton(grantee);
    }

    @Override
    public Set<User> getVisibleUsers(User requestingUser) {
        final GranteeAuthorizer userAuthorizer = granteeAuthorizerFactory.create(requestingUser);

        if (userAuthorizer.isPermitted(RestPermissions.USERS_LIST)) {
            return userService.loadAll().stream().collect(ImmutableSet.toImmutableSet());
        } else {
            return userService.loadAll().stream()
                    .filter(u -> userAuthorizer.isPermitted(RestPermissions.USERS_READ, u.getName()))
                    .collect(ImmutableSet.toImmutableSet());
        }
    }

    private ImmutableSet<AvailableGrantee> getAvailableUserGrantees(User sharingUser) {
        return getVisibleUsers(sharingUser).stream()
                // Don't return the sharing user in available grantees until we want to support that sharing users
                // can remove themselves from an entity.
                .filter(user -> !sharingUser.getId().equals(user.getId()))
                .map(user -> AvailableGrantee.create(
                        grnRegistry.ofUser(user),
                        "user",
                        user.getFullName()
                ))
                .collect(ImmutableSet.toImmutableSet());
    }

    private AvailableGrantee getGlobalGrantee() {
        return AvailableGrantee.create(
                GRNRegistry.GLOBAL_USER_GRN,
                "global",
                "Everyone"
        );
    }

}
