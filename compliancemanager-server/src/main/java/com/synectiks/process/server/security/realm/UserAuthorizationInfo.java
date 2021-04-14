/*
 * */
package com.synectiks.process.server.security.realm;

import org.apache.shiro.authz.SimpleAuthorizationInfo;

import com.synectiks.process.server.plugin.database.users.User;

import java.util.Set;

public class UserAuthorizationInfo extends SimpleAuthorizationInfo {
    private final User user;

    public UserAuthorizationInfo(User user) {
        super();
        this.user = user;
    }

    public UserAuthorizationInfo(Set<String> roles, User user) {
        super(roles);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
