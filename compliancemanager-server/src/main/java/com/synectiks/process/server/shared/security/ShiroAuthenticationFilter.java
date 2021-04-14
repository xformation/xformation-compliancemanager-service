/*
 * */
package com.synectiks.process.server.shared.security;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;

@Priority(Priorities.AUTHENTICATION)
public class ShiroAuthenticationFilter implements ContainerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(ShiroAuthenticationFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final SecurityContext securityContext = requestContext.getSecurityContext();
        if (securityContext instanceof ShiroSecurityContext) {
            final ShiroSecurityContext context = (ShiroSecurityContext) securityContext;
            final Subject subject = context.getSubject();

            LOG.trace("Authenticating... {}", subject);
            if (!subject.isAuthenticated()) {
                try {
                    LOG.trace("Logging in {}", subject);
                    context.loginSubject();
                } catch (LockedAccountException e) {
                    LOG.debug("Unable to authenticate user, account is locked.", e);
                    throw new NotAuthorizedException(e, "Basic realm=\"alertmanager Server\"");
                } catch (AuthenticationException e) {
                    LOG.debug("Unable to authenticate user.", e);
                    throw new NotAuthorizedException(e, "Basic realm=\"alertmanager Server\"");
                }
            }
        } else {
            throw new NotAuthorizedException("Basic realm=\"alertmanager Server\"");
        }

    }
}