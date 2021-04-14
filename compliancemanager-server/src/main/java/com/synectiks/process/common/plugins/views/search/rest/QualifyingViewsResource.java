/*
 * */
package com.synectiks.process.common.plugins.views.search.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import com.synectiks.process.common.plugins.views.search.views.QualifyingViewsService;
import com.synectiks.process.common.plugins.views.search.views.ViewDTO;
import com.synectiks.process.common.plugins.views.search.views.ViewParameterSummaryDTO;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.plugin.rest.PluginRestResource;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.stream.Collectors;

@Api(value = "Views/QualifyingViews")
@Path("/views/forValue")
@Produces(MediaType.APPLICATION_JSON)
@RequiresAuthentication
public class QualifyingViewsResource extends RestResource implements PluginRestResource {
    private final QualifyingViewsService qualifyingViewsService;

    @Inject
    public QualifyingViewsResource(QualifyingViewsService qualifyingViewsService) {
        this.qualifyingViewsService = qualifyingViewsService;
    }

    @POST
    @ApiOperation("Get all views that match given parameter value")
    @NoAuditEvent("Only returning matching views, not changing any data")
    public Collection<ViewParameterSummaryDTO> forParameter() {
        return qualifyingViewsService.forValue()
                .stream()
                .filter(view -> isPermitted(ViewsRestPermissions.VIEW_READ, view.id())
                        || (view.type().equals(ViewDTO.Type.DASHBOARD) && isPermitted(RestPermissions.DASHBOARDS_READ, view.id())))
                .collect(Collectors.toSet());
    }
}
