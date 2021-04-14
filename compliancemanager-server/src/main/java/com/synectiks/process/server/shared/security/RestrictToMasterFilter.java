/*
 * */
package com.synectiks.process.server.shared.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;

@Priority(Priorities.AUTHORIZATION)
public class RestrictToMasterFilter implements ContainerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(RestrictToMasterFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOG.warn("Rejected request to <{}> which is only allowed against master nodes.", requestContext.getUriInfo().getPath());
        throw new ForbiddenException("Request is only allowed against master nodes.");
    }
}
