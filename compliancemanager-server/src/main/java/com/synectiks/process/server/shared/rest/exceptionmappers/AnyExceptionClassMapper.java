/*
 * */
package com.synectiks.process.server.shared.rest.exceptionmappers;

import org.glassfish.jersey.spi.ExtendedExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.plugin.rest.ApiError;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import static com.google.common.base.Strings.nullToEmpty;

@Provider
public class AnyExceptionClassMapper implements ExtendedExceptionMapper<Exception> {
    private static final Logger LOG = LoggerFactory.getLogger(AnyExceptionClassMapper.class);

    @Override
    public boolean isMappable(Exception exception) {
        // we map anything except WebApplicationException to a response, WAEs are handled by the framework.
        return !(exception instanceof WebApplicationException);
    }

    @Override
    public Response toResponse(Exception exception) {
        LOG.error("Unhandled exception in REST resource", exception);
        final String message = nullToEmpty(exception.getMessage());
        final ApiError apiError = ApiError.create(message);

        return Response.serverError()
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(apiError)
                .build();
    }
}
