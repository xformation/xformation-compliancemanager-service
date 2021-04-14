/*
 * */
package com.synectiks.process.server.rest.resources.cluster;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.cluster.Node;
import com.synectiks.process.server.cluster.NodeNotFoundException;
import com.synectiks.process.server.cluster.NodeService;
import com.synectiks.process.server.rest.RemoteInterfaceProvider;
import com.synectiks.process.server.shared.rest.resources.ProxiedResource;
import com.synectiks.process.server.shared.rest.resources.system.RemoteLoadBalancerStatusResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import static javax.ws.rs.core.Response.Status.BAD_GATEWAY;

@RequiresAuthentication
@Api(value = "Cluster/LoadBalancers", description = "Cluster-wide status propagation for LB")
@Produces(MediaType.APPLICATION_JSON)
@Path("/cluster/{nodeId}/lbstatus")
public class ClusterLoadBalancerStatusResource extends ProxiedResource {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterLoadBalancerStatusResource.class);

    @Inject
    public ClusterLoadBalancerStatusResource(NodeService nodeService,
                                             RemoteInterfaceProvider remoteInterfaceProvider,
                                             @Context HttpHeaders httpHeaders,
                                             @Named("proxiedRequestsExecutorService") ExecutorService executorService) throws NodeNotFoundException {
        super(httpHeaders, nodeService, remoteInterfaceProvider, executorService);
    }

    @PUT
    @Timed
    @RequiresAuthentication
    @RequiresPermissions(RestPermissions.LBSTATUS_CHANGE)
    @ApiOperation(value = "Override load balancer status of this alertmanager-server node. Next lifecycle " +
            "change will override it again to its default. Set to ALIVE, DEAD, or THROTTLED.")
    @Path("/override/{status}")
    @NoAuditEvent("this is a proxy resource, the audit event will be emitted on the target node")
    public void override(@ApiParam(name = "nodeId", value = "The id of the node whose LB status will be changed", required = true)
                         @PathParam("nodeId") String nodeId,
                         @ApiParam(name = "status") @PathParam("status") String status) throws IOException, NodeNotFoundException {
        final Node targetNode = nodeService.byNodeId(nodeId);

        RemoteLoadBalancerStatusResource remoteLoadBalancerStatusResource = remoteInterfaceProvider.get(targetNode,
                this.authenticationToken,
                RemoteLoadBalancerStatusResource.class);
        final Response response = remoteLoadBalancerStatusResource.override(status).execute();
        if (!response.isSuccessful()) {
            LOG.warn("Unable to override load balancer status on node {}: {}", nodeId, response.message());
            throw new WebApplicationException(response.message(), BAD_GATEWAY);
        }
    }
}

