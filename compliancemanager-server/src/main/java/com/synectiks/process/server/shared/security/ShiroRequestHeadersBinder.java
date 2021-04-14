/*
 * */
package com.synectiks.process.server.shared.security;

import joptsimple.internal.Strings;
import org.apache.shiro.util.ThreadContext;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.Optional;

/**
 * This filter makes the request headers accessible within Shiro's {@link ThreadContext}.
 */
// Needs to run after RequestIdFilter
@Priority(Priorities.AUTHORIZATION - 10)
public class ShiroRequestHeadersBinder implements ContainerRequestFilter {
    public static final String REQUEST_HEADERS = "REQUEST_HEADERS";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final MultivaluedMap<String, String> headers = requestContext.getHeaders();
        ThreadContext.put(REQUEST_HEADERS, headers);
    }

    public static Optional<String> getHeaderFromThreadContext(String headerName) {
        @SuppressWarnings("unchecked")
        final MultivaluedMap<String, String> requestHeaders =
                (MultivaluedMap<String, String>) ThreadContext.get(REQUEST_HEADERS);
        if (requestHeaders != null) {
            final String header = requestHeaders.getFirst(headerName);
            if (!Strings.isNullOrEmpty(header)) {
                return Optional.of(header);
            }
        }
        return Optional.empty();
    }
}
