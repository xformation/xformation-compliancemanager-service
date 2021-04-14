/*
 * */
package com.synectiks.process.server.shared.security;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.rest.RestTools;

import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.util.Arrays;

@Priority(Priorities.AUTHORIZATION)
public class ShiroAuthorizationFilter implements ContainerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(ShiroAuthorizationFilter.class);
    private final RequiresPermissions annotation;

    public ShiroAuthorizationFilter(RequiresPermissions annotation) {
        this.annotation = annotation;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final SecurityContext securityContext = requestContext.getSecurityContext();
        if (securityContext instanceof ShiroSecurityContext) {
            final ShiroSecurityContext context = (ShiroSecurityContext) securityContext;
            final String userId = RestTools.getUserIdFromRequest(requestContext);
            final ContextAwarePermissionAnnotationHandler annotationHandler = new ContextAwarePermissionAnnotationHandler(context);
            final String[] requiredPermissions = annotation.value();
            try {
                LOG.debug("Checking authorization for user [{}], needs permissions: {}", userId, requiredPermissions);
                annotationHandler.assertAuthorized(annotation);
            } catch (AuthorizationException e) {
                LOG.info("Not authorized. User <{}> is missing permissions {} to perform <{} {}>",
                        userId, Arrays.toString(requiredPermissions), requestContext.getMethod(), requestContext.getUriInfo().getPath());
                throw new ForbiddenException("Not authorized");
            }
        } else {
            throw new ForbiddenException();
        }
    }
}
