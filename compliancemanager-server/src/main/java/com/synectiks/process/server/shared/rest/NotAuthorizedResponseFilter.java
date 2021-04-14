/*
 * */
package com.synectiks.process.server.shared.rest;

import com.google.common.net.HttpHeaders;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class NotAuthorizedResponseFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (responseContext.getStatusInfo().equals(Response.Status.UNAUTHORIZED)) {
            final String requestedWith = requestContext.getHeaderString(HttpHeaders.X_REQUESTED_WITH);
            if ("XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
                responseContext.getHeaders().remove(HttpHeaders.WWW_AUTHENTICATE);

            }
        }
    }
}
