/*
 * */
package com.synectiks.process.server.rest;

import com.synectiks.process.server.indexer.ElasticsearchException;
import com.synectiks.process.server.rest.resources.search.responses.SearchError;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ElasticsearchExceptionMapper implements ExceptionMapper<ElasticsearchException> {
    @Override
    public Response toResponse(ElasticsearchException exception) {
        final SearchError searchError = SearchError.create(exception.getMessage(), exception.getErrorDetails());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(searchError).build();
    }
}
