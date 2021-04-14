/*
 * */
package com.synectiks.process.server.shared.rest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

public class XHRFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        // Add no-cache to XMLHttpRequests, to avoid browsers caching results
        String requestedWith = requestContext.getHeaders().getFirst("X-Requested-With");
        if ("XMLHttpRequest".equals(requestedWith)) {
            responseContext.getHeaders().add("Cache-Control", "no-cache");
        }
    }
}
