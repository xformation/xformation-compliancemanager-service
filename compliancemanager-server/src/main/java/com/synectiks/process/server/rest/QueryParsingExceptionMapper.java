/*
 * */
package com.synectiks.process.server.rest;

import com.synectiks.process.server.indexer.QueryParsingException;
import com.synectiks.process.server.rest.resources.search.responses.QueryParseError;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class QueryParsingExceptionMapper implements ExceptionMapper<QueryParsingException> {
    @Override
    public Response toResponse(QueryParsingException exception) {
        final QueryParseError errorMessage = QueryParseError.create(
                exception.getMessage(),
                exception.getErrorDetails(),
                exception.getLine().orElse(null),
                exception.getColumn().orElse(null));

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorMessage)
                .build();
    }
}
