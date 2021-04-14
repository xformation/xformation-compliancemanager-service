/*
 * */
package com.synectiks.process.common.security;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNRegistry;
import com.synectiks.process.server.plugin.database.users.User;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class GranteeAuthorizer {
    public interface Factory {
        GranteeAuthorizer create(GRN grantee);
        GranteeAuthorizer create(User grantee);
    }

    private final Subject subject;

    @AssistedInject
    public GranteeAuthorizer(DefaultSecurityManager securityManager,
                             GRNRegistry grnRegistry,
                             @Assisted User grantee) {
        this(securityManager, grnRegistry.ofUser(grantee));
    }

    @AssistedInject
    public GranteeAuthorizer(DefaultSecurityManager securityManager, @Assisted GRN grantee) {
        this.subject = new Subject.Builder(securityManager)
                .principals(new SimplePrincipalCollection(grantee, "GranteeAuthorizer"))
                .authenticated(true)
                .sessionCreationEnabled(false)
                .buildSubject();
    }

    public boolean isPermitted(String permission, GRN target) {
        return isPermitted(permission, target.entity());
    }

    public boolean isPermitted(String permission) {
        checkArgument(isNotBlank(permission), "permission cannot be null or empty");
        return subject.isPermitted(permission);
    }

    public boolean isPermitted(String permission, String id) {
        checkArgument(isNotBlank(permission), "permission cannot be null or empty");
        checkArgument(isNotBlank(id), "id cannot be null or empty");

        return subject.isPermitted(permission + ":" + id);
    }
}
