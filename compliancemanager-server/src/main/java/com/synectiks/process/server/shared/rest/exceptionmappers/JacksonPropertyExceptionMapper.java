/*
 * */
package com.synectiks.process.server.shared.rest.exceptionmappers;

import com.fasterxml.jackson.databind.exc.PropertyBindingException;
import com.google.common.base.Joiner;
import com.synectiks.process.server.plugin.rest.ApiError;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collection;
import java.util.Collections;

import static com.google.common.base.MoreObjects.firstNonNull;
import static javax.ws.rs.core.Response.status;

@Provider
public class JacksonPropertyExceptionMapper implements ExceptionMapper<PropertyBindingException> {
    @Override
    public Response toResponse(PropertyBindingException e) {
        final Collection<Object> knownPropertyIds = firstNonNull(e.getKnownPropertyIds(), Collections.emptyList());
        final StringBuilder message = new StringBuilder("Unable to map property ")
                .append(e.getPropertyName())
                .append(".\nKnown properties include: ");
        Joiner.on(", ").appendTo(message, knownPropertyIds);
        final ApiError apiError = ApiError.create(message.toString());
        return status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(apiError).build();
    }
}
