/*
 * */
package com.synectiks.process.server.shared.rest;

import com.google.common.base.Strings;
import com.synectiks.process.common.util.uuid.ConcurrentUUID;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;

// Needs to run before ShiroAuthorizationFilter
@Priority(Priorities.AUTHORIZATION - 20)
public class RequestIdFilter implements ContainerRequestFilter {
    public final static String X_REQUEST_ID = "X-Request-Id";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String id = requestContext.getHeaderString(X_REQUEST_ID);
        if (Strings.isNullOrEmpty(id)) {
            id = ConcurrentUUID.generateRandomUuid().toString();
        }
        requestContext.getHeaders().putSingle(X_REQUEST_ID, id);
    }
}
