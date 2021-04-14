/*
 * */
package com.synectiks.process.server.security.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.security.AccessToken;
import com.synectiks.process.server.security.AccessTokenService;
import com.synectiks.process.server.shared.security.AccessTokenAuthToken;
import com.synectiks.process.server.shared.security.ShiroSecurityContext;
import com.synectiks.process.server.shared.users.UserService;

import javax.inject.Inject;

public class AccessTokenAuthenticator extends AuthenticatingRealm {
    private static final Logger LOG = LoggerFactory.getLogger(AccessTokenAuthenticator.class);
    public static final String NAME = "access-token";

    private final AccessTokenService accessTokenService;
    private final UserService userService;

    @Inject
    AccessTokenAuthenticator(AccessTokenService accessTokenService,
                             UserService userService) {
        this.accessTokenService = accessTokenService;
        this.userService = userService;
        setAuthenticationTokenClass(AccessTokenAuthToken.class);
        setCachingEnabled(false);
        // the presence of a valid access token is enough, we don't have any other credentials
        setCredentialsMatcher(new AllowAllCredentialsMatcher());
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        AccessTokenAuthToken authToken = (AccessTokenAuthToken) token;
        final AccessToken accessToken = accessTokenService.load(String.valueOf(authToken.getToken()));

        if (accessToken == null) {
            return null;
        }
        // TODO should be using IDs
        final User user = userService.load(accessToken.getUserName());
        if (user == null) {
            return null;
        }
        if (!user.getAccountStatus().equals(User.AccountStatus.ENABLED)) {
            LOG.warn("Account for user <{}> is disabled.", user.getName());
            return null;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Found user {} for access token.", user);
        }
        try {
            accessTokenService.touch(accessToken);
        } catch (ValidationException e) {
            LOG.warn("Unable to update access token's last access date.", e);
        }
        ShiroSecurityContext.requestSessionCreation(false);
        return new SimpleAccount(user.getId(), null, "access token realm");
    }
}
