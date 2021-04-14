/*
 * */
package com.synectiks.process.server.rest;

import javax.ws.rs.core.Response;

/**
 * A {@link Response.StatusType} for HTTP status 429 (Too many requests).
 */
public class TooManyRequestsStatus implements Response.StatusType {
    @Override
    public int getStatusCode() {
        return 429;
    }

    @Override
    public Response.Status.Family getFamily() {
        return Response.Status.Family.CLIENT_ERROR;
    }

    @Override
    public String getReasonPhrase() {
        return "Too many requests";
    }
}
