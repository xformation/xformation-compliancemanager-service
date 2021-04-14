/*
 * */
package com.synectiks.process.server.rest;

import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.rest.ApiError;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException exception) {
        final ApiError apiError = ApiError.create(exception.getMessage());
        return Response.status(Response.Status.NOT_FOUND).entity(apiError).build();
    }
}
