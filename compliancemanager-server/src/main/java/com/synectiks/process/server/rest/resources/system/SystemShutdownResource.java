/*
 * */
package com.synectiks.process.server.rest.resources.system;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.audit.AuditEventTypes;
import com.synectiks.process.server.audit.jersey.AuditEvent;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;
import com.synectiks.process.server.system.shutdown.GracefulShutdown;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.accepted;

@RequiresAuthentication
@Api(value = "System/Shutdown", description = "Shutdown this node gracefully.")
@Path("/system/shutdown")
public class SystemShutdownResource extends RestResource {
    private final GracefulShutdown gracefulShutdown;
    private final ServerStatus serverStatus;

    @Inject
    public SystemShutdownResource(GracefulShutdown gracefulShutdown,
                                  ServerStatus serverStatus) {
        this.gracefulShutdown = gracefulShutdown;
        this.serverStatus = serverStatus;
    }

    @POST
    @Timed
    @ApiOperation(value = "Shutdown this node gracefully.",
            notes = "Attempts to process all buffered and cached messages before exiting, " +
                    "shuts down inputs first to make sure that no new messages are accepted.")
    @Path("/shutdown")
    @AuditEvent(type = AuditEventTypes.NODE_SHUTDOWN_INITIATE)
    public Response shutdown() {
        checkPermission(RestPermissions.NODE_SHUTDOWN, serverStatus.getNodeId().toString());

        new Thread(gracefulShutdown).start();
        return accepted().build();
    }
}
