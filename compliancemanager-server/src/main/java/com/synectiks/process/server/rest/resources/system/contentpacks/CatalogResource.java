/*
 * */
package com.synectiks.process.server.rest.resources.system.contentpacks;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.contentpacks.ContentPackService;
import com.synectiks.process.server.contentpacks.model.entities.Entity;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.EntityExcerpt;
import com.synectiks.process.server.rest.models.system.contentpacks.responses.CatalogIndexResponse;
import com.synectiks.process.server.rest.models.system.contentpacks.responses.CatalogResolveRequest;
import com.synectiks.process.server.rest.models.system.contentpacks.responses.CatalogResolveResponse;
import com.synectiks.process.server.shared.security.RestPermissions;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Set;

@RequiresAuthentication
@Api(value = "System/Catalog", description = "Entity Catalog")
@Path("/system/catalog")
@Produces(MediaType.APPLICATION_JSON)
public class CatalogResource {
    private final ContentPackService contentPackService;

    @Inject
    public CatalogResource(ContentPackService contentPackService) {
        this.contentPackService = contentPackService;
    }

    @GET
    @Timed
    @ApiOperation(value = "List available entities in this alertmanager cluster")
    @RequiresPermissions(RestPermissions.CATALOG_LIST)
    public CatalogIndexResponse showEntityIndex() {
        final Set<EntityExcerpt> entities = contentPackService.listAllEntityExcerpts();
        return CatalogIndexResponse.create(entities);
    }

    @POST
    @Timed
    @ApiOperation(value = "Resolve dependencies of entities and return their configuration")
    @RequiresPermissions(RestPermissions.CATALOG_RESOLVE)
    @NoAuditEvent("this is not changing any data")
    public CatalogResolveResponse resolveEntities(
            @ApiParam(name = "JSON body", required = true)
            @Valid @NotNull CatalogResolveRequest request) {
        final Set<EntityDescriptor> requestedEntities = request.entities();
        final Set<EntityDescriptor> resolvedEntities = contentPackService.resolveEntities(requestedEntities);
        final ImmutableSet<Entity> entities = contentPackService.collectEntities(resolvedEntities);

        return CatalogResolveResponse.create(entities);
    }
}
