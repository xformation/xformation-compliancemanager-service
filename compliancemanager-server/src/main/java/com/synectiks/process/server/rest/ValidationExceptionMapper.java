/*
 * */
package com.synectiks.process.server.rest;

import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.rest.ValidationApiError;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {
    @Override
    public Response toResponse(ValidationException exception) {
        final ValidationApiError error = ValidationApiError.create("Validation failed!", exception.getErrors());
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(error)
                .build();
    }
}
