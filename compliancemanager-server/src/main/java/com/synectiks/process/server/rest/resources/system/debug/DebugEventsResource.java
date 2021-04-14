/*
 * */
package com.synectiks.process.server.rest.resources.system.debug;

import com.codahale.metrics.annotation.Timed;
import com.google.common.eventbus.EventBus;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.events.ClusterEventBus;
import com.synectiks.process.server.plugin.system.NodeId;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.system.debug.DebugEvent;
import com.synectiks.process.server.system.debug.DebugEventHolder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

@RequiresAuthentication
@Api(value = "System/Debug/Events", description = "For debugging local and cluster events.")
@Path("/system/debug/events")
@Produces(MediaType.APPLICATION_JSON)
public class DebugEventsResource extends RestResource {
    private final NodeId nodeId;
    private final EventBus serverEventBus;
    private final EventBus clusterEventBus;

    @Inject
    public DebugEventsResource(NodeId nodeId,
                               EventBus serverEventBus,
                               ClusterEventBus clusterEventBus) {
        this.nodeId = checkNotNull(nodeId);
        this.serverEventBus = checkNotNull(serverEventBus);
        this.clusterEventBus = checkNotNull(clusterEventBus);
    }

    @Timed
    @POST
    @Path("/cluster")
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Create and send a cluster debug event.")
    @NoAuditEvent("only used to create a debug event")
    public void generateClusterDebugEvent(@ApiParam(name = "text", defaultValue = "Cluster Test") @Nullable String text) {
        clusterEventBus.post(DebugEvent.create(nodeId.toString(), isNullOrEmpty(text) ? "Cluster Test" : text));
    }

    @Timed
    @POST
    @Path("/local")
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Create and send a local debug event.")
    @NoAuditEvent("only used to create a debug event")
    public void generateDebugEvent(@ApiParam(name = "text", defaultValue = "Local Test") @Nullable String text) {
        serverEventBus.post(DebugEvent.create(nodeId.toString(), isNullOrEmpty(text) ? "Local Test" : text));
    }

    @Timed
    @GET
    @Path("/cluster")
    @ApiOperation(value = "Show last received cluster debug event.", response = DebugEvent.class)
    public DebugEvent showLastClusterDebugEvent() {
        return DebugEventHolder.getClusterDebugEvent();
    }

    @Timed
    @GET
    @Path("/local")
    @ApiOperation(value = "Show last received local debug event.", response = DebugEvent.class)
    public DebugEvent showLastDebugEvent() {
        return DebugEventHolder.getLocalDebugEvent();
    }
}
