/*
 * */
package com.synectiks.process.server.shared.rest;

import org.glassfish.jersey.server.filter.CsrfProtectionFilter;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.container.ContainerRequestContext;
import java.io.IOException;

public class VerboseCsrfProtectionFilter extends CsrfProtectionFilter {
    @Override
    public void filter(ContainerRequestContext rc) throws IOException {
        try {
            // Backward compatibility for Sidecars < 0.1.7
            if (!rc.getHeaders().containsKey("X-alertmanager-Collector-Version")) {
                super.filter(rc);
            }
        } catch (BadRequestException badRequestException) {
            throw new BadRequestException(
                    "CSRF protection header is missing. Please add a \"" + HEADER_NAME + "\" header to your request.",
                    badRequestException
            );
        }
    }
}
