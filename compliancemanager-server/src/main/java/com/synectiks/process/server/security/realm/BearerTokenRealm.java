/*
 * */
package com.synectiks.process.server.security.realm;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.security.authservice.AuthServiceAuthenticator;
import com.synectiks.process.common.security.authservice.AuthServiceException;
import com.synectiks.process.common.security.authservice.AuthServiceResult;
import com.synectiks.process.common.security.authservice.AuthServiceToken;
import com.synectiks.process.server.shared.security.AuthenticationServiceUnavailableException;
import com.synectiks.process.server.shared.security.TypedBearerToken;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class BearerTokenRealm extends AuthenticatingRealm {
    private static final Logger log = LoggerFactory.getLogger(BearerTokenRealm.class);

    public static final String NAME = "bearer-token";

    private final AuthServiceAuthenticator authenticator;

    @Inject
    public BearerTokenRealm(AuthServiceAuthenticator authenticator) {
        this.authenticator = authenticator;

        setAuthenticationTokenClass(TypedBearerToken.class);
        setCachingEnabled(false);

        // Credentials will be matched via the authentication service itself so we don't need Shiro to do it
        setCredentialsMatcher(new AllowAllCredentialsMatcher());
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        if (authToken instanceof TypedBearerToken) {
            return doGetAuthenticationInfo((TypedBearerToken) authToken);
        }
        throw new UnsupportedTokenException("Unsupported authentication token type: " + authToken.getClass());
    }

    private AuthenticationInfo doGetAuthenticationInfo(TypedBearerToken token) throws AuthenticationException {
        log.debug("Attempting authentication for bearer token of type <{}>.",
                token.getType());
        try {
            final AuthServiceResult result = authenticator.authenticate(AuthServiceToken.builder()
                    .token(token.getToken())
                    .type(token.getType())
                    .build());

            if (result.isSuccess()) {
                log.debug("Successfully authenticated username <{}> for user profile <{}> with backend <{}/{}/{}>",
                        result.username(), result.userProfileId(), result.backendTitle(), result.backendType(),
                        result.backendId());
                return toAuthenticationInfo(result);
            } else {
                log.warn("Failed to authenticate username <{}> with backend <{}/{}/{}>",
                        result.username(), result.backendTitle(), result.backendType(), result.backendId());
                return null;
            }
        } catch (AuthServiceException e) {
            throw new AuthenticationServiceUnavailableException(e);
        } catch (Exception e) {
            log.error("Unhandled authentication error", e);
            return null;
        }
    }

    private AuthenticationInfo toAuthenticationInfo(AuthServiceResult result) {
        String realmName = NAME + "/" + result.backendType();

        @SuppressWarnings("ConstantConditions")
        final SimplePrincipalCollection principals = new SimplePrincipalCollection(
                ImmutableList.of(result.userProfileId(), result.sessionAttributes()), realmName);

        return new SimpleAccount(principals, null, realmName);
    }
}
