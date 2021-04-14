/*
 * */
package com.synectiks.process.server.rest.filter;

import com.synectiks.process.server.configuration.HttpConfiguration;
import com.synectiks.process.server.web.IndexHtmlGenerator;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

@Priority(Priorities.ENTITY_CODER)
public class WebAppNotFoundResponseFilter implements ContainerResponseFilter {
    private final IndexHtmlGenerator indexHtmlGenerator;

    @Inject
    public WebAppNotFoundResponseFilter(IndexHtmlGenerator indexHtmlGenerator) {
        this.indexHtmlGenerator = indexHtmlGenerator;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        final Response.StatusType responseStatus = responseContext.getStatusInfo();
        final String requestPath = requestContext.getUriInfo().getAbsolutePath().getPath();
        final List<MediaType> acceptableMediaTypes = requestContext.getAcceptableMediaTypes();
        final boolean acceptsHtml = acceptableMediaTypes.stream()
                .anyMatch(mediaType -> mediaType.isCompatible(MediaType.TEXT_HTML_TYPE) || mediaType.isCompatible(MediaType.APPLICATION_XHTML_XML_TYPE));
        final boolean isGetRequest = "get".equalsIgnoreCase(requestContext.getMethod());

        if (isGetRequest
                && responseStatus == Response.Status.NOT_FOUND
                && acceptsHtml
                && !requestPath.startsWith("/" + HttpConfiguration.PATH_API)) {
            final String entity = indexHtmlGenerator.get(requestContext.getHeaders());
            responseContext.setStatusInfo(Response.Status.OK);
            responseContext.setEntity(entity, new Annotation[0], MediaType.TEXT_HTML_TYPE);

            responseContext.getHeaders().putSingle("X-UA-Compatible", "IE=edge");
        }
    }
}
