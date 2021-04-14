/*
 * */
package com.synectiks.process.common.plugins.views.search.rest.exceptionmappers;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.common.plugins.views.search.errors.MissingCapabilitiesException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Map;

public class MissingCapabilitiesExceptionMapper implements ExceptionMapper<MissingCapabilitiesException> {
    @Override
    public Response toResponse(MissingCapabilitiesException exception) {
        final Map<String, Object> error = ImmutableMap.of(
                "error", "Unable to execute this search, the following capabilities are missing:",
                "missing", exception.getMissingRequirements()
        );
        return Response.status(Response.Status.CONFLICT).entity(error).build();
    }
}
