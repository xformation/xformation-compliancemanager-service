/*
 * */
package com.synectiks.process.server.shared.rest.exceptionmappers;

import org.glassfish.jersey.spi.ExtendedExceptionMapper;

import com.synectiks.process.server.plugin.rest.ApiError;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper implements ExtendedExceptionMapper<BadRequestException> {
    @Override
    public boolean isMappable(BadRequestException exception) {
        return true;
    }

    @Override
    public Response toResponse(BadRequestException e) {
        final ApiError apiError = ApiError.create(e.getMessage());

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(apiError)
                .build();
    }
}
