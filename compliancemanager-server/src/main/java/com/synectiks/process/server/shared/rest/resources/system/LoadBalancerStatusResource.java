/*
 * */
package com.synectiks.process.server.shared.rest.resources.system;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.audit.AuditEventTypes;
import com.synectiks.process.server.audit.jersey.AuditEvent;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.lifecycles.LoadBalancerStatus;
import com.synectiks.process.server.rest.TooManyRequestsStatus;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Locale;

@Api(value = "System/LoadBalancers", description = "Status propagation for load balancers")
@Path("/system/lbstatus")
public class LoadBalancerStatusResource extends RestResource {

    /*
     *  IMPORTANT: this resource is unauthenticated to allow easy
     *             acccess for load balancers. think about this
     *             when adding more stuff.
     */

    private final ServerStatus serverStatus;

    @Inject
    public LoadBalancerStatusResource(ServerStatus serverStatus) {
        this.serverStatus = serverStatus;
    }

    @GET
    @Timed
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Get status of this alertmanager server node for load balancers. " +
            "Returns ALIVE with HTTP 200, DEAD with HTTP 503, or THROTTLED with HTTP 429.")
    public Response status() {
        final LoadBalancerStatus lbStatus = serverStatus.getLifecycle().getLoadbalancerStatus();

        Response.StatusType status;
        switch (lbStatus) {
            case ALIVE:
                status = Response.Status.OK;
                break;
            case THROTTLED:
                status = new TooManyRequestsStatus();
                break;
            default:
                status = Response.Status.SERVICE_UNAVAILABLE;
        }

        return Response.status(status)
                .entity(lbStatus.toString().toUpperCase(Locale.ENGLISH))
                .build();
    }

    @PUT
    @Timed
    @RequiresAuthentication
    @RequiresPermissions(RestPermissions.LBSTATUS_CHANGE)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Override load balancer status of this alertmanager server node. Next lifecycle " +
            "change will override it again to its default. Set to ALIVE, DEAD, or THROTTLED.")
    @Path("/override/{status}")
    @AuditEvent(type = AuditEventTypes.LOAD_BALANCER_STATUS_UPDATE)
    public void override(@ApiParam(name = "status") @PathParam("status") String status) {
        final LoadBalancerStatus lbStatus;
        try {
            lbStatus = LoadBalancerStatus.valueOf(status.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e);
        }

        switch (lbStatus) {
            case DEAD:
                serverStatus.overrideLoadBalancerDead();
                break;
            case ALIVE:
                serverStatus.overrideLoadBalancerAlive();
                break;
            case THROTTLED:
                serverStatus.overrideLoadBalancerThrottled();
        }
    }
}
