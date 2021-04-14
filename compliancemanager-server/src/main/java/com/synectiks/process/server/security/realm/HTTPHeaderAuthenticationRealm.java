/*
 * */
package com.synectiks.process.server.security.realm;

import com.google.common.base.Joiner;
import com.synectiks.process.common.security.authservice.AuthServiceAuthenticator;
import com.synectiks.process.common.security.authservice.AuthServiceCredentials;
import com.synectiks.process.common.security.authservice.AuthServiceException;
import com.synectiks.process.common.security.authservice.AuthServiceResult;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.security.headerauth.HTTPHeaderAuthConfig;
import com.synectiks.process.server.shared.security.HttpHeadersToken;
import com.synectiks.process.server.shared.security.ShiroSecurityContext;
import com.synectiks.process.server.utilities.IpSubnet;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.MultivaluedMap;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class HTTPHeaderAuthenticationRealm extends AuthenticatingRealm {
    private static final Logger LOG = LoggerFactory.getLogger(HTTPHeaderAuthenticationRealm.class);
    private static final Joiner JOINER = Joiner.on(", ");

    public static final String NAME = "http-header-authentication";
    public static final String SESSION_AUTH_HEADER = "http-header-auth-user";

    private final ClusterConfigService clusterConfigService;
    private final AuthServiceAuthenticator authServiceAuthenticator;
    private final Set<IpSubnet> trustedProxies;

    @Inject
    public HTTPHeaderAuthenticationRealm(ClusterConfigService clusterConfigService,
                                         AuthServiceAuthenticator authServiceAuthenticator,
                                         @Named("trusted_proxies") Set<IpSubnet> trustedProxies) {
        this.clusterConfigService = clusterConfigService;
        this.authServiceAuthenticator = authServiceAuthenticator;
        this.trustedProxies = trustedProxies;

        setAuthenticationTokenClass(HttpHeadersToken.class);
        setCachingEnabled(false);
        // Credentials will be matched via the authentication service itself so we don't need Shiro to do it
        setCredentialsMatcher(new AllowAllCredentialsMatcher());
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        final HttpHeadersToken headersToken = (HttpHeadersToken) token;
        final HTTPHeaderAuthConfig config = loadConfig();

        if (!config.enabled()) {
            LOG.debug("Skipping disabled HTTP header authentication");
            return null;
        }

        final MultivaluedMap<String, String> headers = headersToken.getHeaders();
        final Optional<String> optionalUsername = headerValue(headers, config.usernameHeader());

        if (optionalUsername.isPresent()) {
            final String username = optionalUsername.get().trim();

            if (isBlank(username)) {
                LOG.warn("Skipping request with trusted HTTP header <{}> and blank value", config.usernameHeader());
                return null;
            }

            final String remoteAddr = headersToken.getRemoteAddr();
            if (inTrustedSubnets(remoteAddr)) {
                return doAuthenticate(username, config, remoteAddr);
            }

            LOG.warn("Request with trusted HTTP header <{}={}> received from <{}> which is not in the trusted proxies: <{}>",
                    config.usernameHeader(),
                    username,
                    remoteAddr,
                    JOINER.join(trustedProxies));
            return null;
        }

        return null;
    }

    private AuthenticationInfo doAuthenticate(String username, HTTPHeaderAuthConfig config, String remoteAddr) {
        LOG.debug("Attempting authentication for username <{}>", username);
        try {
            // Create already authenticated credentials to make sure the auth service backend doesn't try to
            // authenticate the user again
            final AuthServiceCredentials credentials = AuthServiceCredentials.createAuthenticated(username);
            final AuthServiceResult result = authServiceAuthenticator.authenticate(credentials);

            if (result.isSuccess()) {
                LOG.debug("Successfully authenticated username <{}> for user profile <{}> with backend <{}/{}/{}>",
                        result.username(), result.userProfileId(), result.backendTitle(), result.backendType(), result.backendId());
                // Setting this, will let the SessionResource know, that when a non-existing session is validated, it
                // should in fact create a session.
                ShiroSecurityContext.requestSessionCreation(true);
                return toAuthenticationInfo(result);
            } else {
                LOG.warn("Failed to authenticate username <{}> from trusted HTTP header <{}> via proxy <{}>",
                        result.username(), config.usernameHeader(), remoteAddr);
                return null;
            }
        } catch (AuthServiceException e) {
            LOG.error("Authentication service error", e);
            return null;
        } catch (Exception e) {
            LOG.error("Unhandled authentication error", e);
            return null;
        }
    }

    private AuthenticationInfo toAuthenticationInfo(AuthServiceResult result) {
        return new SimpleAccount(result.userProfileId(), null, NAME + "/" + result.backendType());
    }

    private HTTPHeaderAuthConfig loadConfig() {
        return clusterConfigService.getOrDefault(HTTPHeaderAuthConfig.class, HTTPHeaderAuthConfig.createDisabled());
    }

    private Optional<String> headerValue(MultivaluedMap<String, String> headers, @Nullable String headerName) {
        if (headerName == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(headers.getFirst(headerName.toLowerCase(Locale.US)));
    }

    private boolean inTrustedSubnets(String remoteAddr) {
        return trustedProxies.stream().anyMatch(ipSubnet -> ipSubnetContains(ipSubnet, remoteAddr));
    }

    private boolean ipSubnetContains(IpSubnet ipSubnet, String ipAddr) {
        try {
            return ipSubnet.contains(ipAddr);
        } catch (UnknownHostException ignored) {
            LOG.debug("Looking up remote address <{}> failed.", ipAddr);
            return false;
        }
    }
}
