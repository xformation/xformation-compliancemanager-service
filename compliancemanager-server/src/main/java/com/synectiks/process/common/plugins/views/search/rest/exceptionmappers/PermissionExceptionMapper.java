/*
 * */
package com.synectiks.process.common.plugins.views.search.rest.exceptionmappers;

import com.synectiks.process.common.plugins.views.search.errors.PermissionException;
import com.synectiks.process.server.plugin.rest.ApiError;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class PermissionExceptionMapper implements ExceptionMapper<PermissionException> {
    @Override
    public Response toResponse(PermissionException exception) {
        final ApiError apiError = ApiError.create(exception.getMessage());
        return Response.status(Response.Status.FORBIDDEN).entity(apiError).build();
    }
}
