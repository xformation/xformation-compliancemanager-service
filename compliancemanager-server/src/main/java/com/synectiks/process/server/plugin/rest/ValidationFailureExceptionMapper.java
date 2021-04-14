/*
 * */
package com.synectiks.process.server.plugin.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationFailureExceptionMapper implements ExceptionMapper<ValidationFailureException> {
    @Override
    public Response toResponse(ValidationFailureException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(exception.getValidationResult())
                .build();
    }
}
