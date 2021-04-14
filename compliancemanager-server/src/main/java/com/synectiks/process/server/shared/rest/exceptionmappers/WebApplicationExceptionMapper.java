/*
 * */
package com.synectiks.process.server.shared.rest.exceptionmappers;

import org.glassfish.jersey.spi.ExtendedExceptionMapper;

import com.synectiks.process.server.plugin.rest.ApiError;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class WebApplicationExceptionMapper implements ExtendedExceptionMapper<WebApplicationException> {
    @Override
    public boolean isMappable(WebApplicationException e) {
        return !(e instanceof NotAuthorizedException) && e != null;
    }

    @Override
    public Response toResponse(WebApplicationException e) {
        final ApiError apiError = ApiError.create(e.getMessage());

        return Response.fromResponse(e.getResponse())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(apiError).build();
    }
}
