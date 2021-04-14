/*
 * */
package com.synectiks.process.server.shared.rest.exceptionmappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synectiks.process.server.plugin.rest.ApiError;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static com.google.common.base.MoreObjects.firstNonNull;
import static javax.ws.rs.core.Response.status;

@Provider
public class JsonProcessingExceptionMapper implements ExceptionMapper<JsonProcessingException> {
    @Override
    public Response toResponse(JsonProcessingException e) {
        final String message = firstNonNull(e.getMessage(), "Couldn't process JSON input");
        final ApiError apiError = ApiError.create(message);
        return status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(apiError).build();
    }
}
