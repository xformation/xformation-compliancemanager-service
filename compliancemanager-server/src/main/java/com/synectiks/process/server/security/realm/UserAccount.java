/*
 * */
package com.synectiks.process.server.security.realm;

import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import com.synectiks.process.server.plugin.database.users.User;

import java.util.Collection;
import java.util.Set;

public class UserAccount extends SimpleAccount {
    private final User user;

    public UserAccount(User user) {
        this.user = user;
    }

    public UserAccount(Object principal, Object credentials, String realmName, User user) {
        super(principal, credentials, realmName);
        this.user = user;
    }

    public UserAccount(Object principal, Object hashedCredentials, ByteSource credentialsSalt, String realmName, User user) {
        super(principal, hashedCredentials, credentialsSalt, realmName);
        this.user = user;
    }

    public UserAccount(Collection principals, Object credentials, String realmName, User user) {
        super(principals, credentials, realmName);
        this.user = user;
    }

    public UserAccount(PrincipalCollection principals, Object credentials, User user) {
        super(principals, credentials);
        this.user = user;
    }

    public UserAccount(PrincipalCollection principals, Object hashedCredentials, ByteSource credentialsSalt, User user) {
        super(principals, hashedCredentials, credentialsSalt);
        this.user = user;
    }

    public UserAccount(PrincipalCollection principals, Object credentials, Set<String> roles, User user) {
        super(principals, credentials, roles);
        this.user = user;
    }

    public UserAccount(Object principal, Object credentials, String realmName, Set<String> roleNames, Set<Permission> permissions, User user) {
        super(principal, credentials, realmName, roleNames, permissions);
        this.user = user;
    }

    public UserAccount(Collection principals, Object credentials, String realmName, Set<String> roleNames, Set<Permission> permissions, User user) {
        super(principals, credentials, realmName, roleNames, permissions);
        this.user = user;
    }

    public UserAccount(PrincipalCollection principals, Object credentials, Set<String> roleNames, Set<Permission> permissions, User user) {
        super(principals, credentials, roleNames, permissions);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
