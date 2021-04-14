/*
 * */
package com.synectiks.process.common.events.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import com.synectiks.process.common.events.search.EventsSearchParameters;
import com.synectiks.process.common.events.search.EventsSearchResult;
import com.synectiks.process.common.events.search.EventsSearchService;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.plugin.rest.PluginRestResource;
import com.synectiks.process.server.shared.rest.resources.RestResource;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static com.google.common.base.MoreObjects.firstNonNull;

@Api(value = "Events", description = "Events overview and search")
@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiresAuthentication
public class EventsResource extends RestResource implements PluginRestResource {
    private final EventsSearchService searchService;

    @Inject
    public EventsResource(EventsSearchService searchService) {
        this.searchService = searchService;
    }

    @POST
    @Path("/search")
    @ApiOperation("Search events")
    @NoAuditEvent("Doesn't change any data, only searches for events")
    public EventsSearchResult search(@ApiParam(name = "JSON body") EventsSearchParameters request) {
        return searchService.search(firstNonNull(request, EventsSearchParameters.empty()), getSubject());
    }
}
