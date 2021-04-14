/*
 * */
package com.synectiks.process.server.rest.resources.system;

import com.google.common.collect.Sets;
import com.synectiks.process.server.audit.AuditEventTypes;
import com.synectiks.process.server.audit.jersey.AuditEvent;
import com.synectiks.process.server.gettingstarted.GettingStartedState;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.rest.models.system.DisplayGettingStarted;
import com.synectiks.process.server.shared.rest.resources.RestResource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Locale;

@RequiresAuthentication
@Api(value = "System/GettingStartedGuides", description = "Getting Started guide")
@Path("/system/gettingstarted")
@Produces(MediaType.APPLICATION_JSON)
public class GettingStartedResource extends RestResource {

    private final ClusterConfigService clusterConfigService;

    @Inject
    public GettingStartedResource(ClusterConfigService clusterConfigService) {
        this.clusterConfigService = clusterConfigService;
    }

    @GET
    @ApiOperation("Check whether to display the Getting started guide for this version")
    public DisplayGettingStarted displayGettingStarted() {
        final GettingStartedState gettingStartedState = clusterConfigService.get(GettingStartedState.class);
        if (gettingStartedState == null) {
            return  DisplayGettingStarted.create(true);
        }
        final boolean isDismissed = gettingStartedState.dismissedInVersions().contains(currentMinorVersionString());
        return DisplayGettingStarted.create(!isDismissed);
    }

    @POST
    @Path("dismiss")
    @ApiOperation("Dismiss auto-showing getting started guide for this version")
    @AuditEvent(type = AuditEventTypes.GETTING_STARTED_GUIDE_OPT_OUT_CREATE)
    public void dismissGettingStarted() {
        final GettingStartedState gettingStartedState = clusterConfigService.getOrDefault(GettingStartedState.class,
                                                                                GettingStartedState.create(Sets.<String>newHashSet()));
        gettingStartedState.dismissedInVersions().add(currentMinorVersionString());
        clusterConfigService.write(gettingStartedState);

    }

    private static String currentMinorVersionString() {
        return String.format(Locale.ENGLISH, "%d.%d",
                             Version.CURRENT_CLASSPATH.getVersion().getMajorVersion(),
                             Version.CURRENT_CLASSPATH.getVersion().getMinorVersion());
    }
}
