/*
 * */
package com.synectiks.process.server.rest.resources.cluster;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.cluster.Node;
import com.synectiks.process.server.cluster.NodeNotFoundException;
import com.synectiks.process.server.cluster.NodeService;
import com.synectiks.process.server.rest.RemoteInterfaceProvider;
import com.synectiks.process.server.rest.resources.system.RemoteSystemProcessingResource;
import com.synectiks.process.server.shared.rest.resources.ProxiedResource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.POST;
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
@Api(value = "Cluster/Processing", description = "Cluster-wide processing status control.")
@Path("/cluster/{nodeId}/processing")
@Produces(MediaType.APPLICATION_JSON)
public class ClusterSystemProcessingResource extends ProxiedResource {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterSystemProcessingResource.class);

    @Inject
    public ClusterSystemProcessingResource(NodeService nodeService,
                                           RemoteInterfaceProvider remoteInterfaceProvider,
                                           @Context HttpHeaders httpHeaders,
                                           @Named("proxiedRequestsExecutorService") ExecutorService executorService) throws NodeNotFoundException {
        super(httpHeaders, nodeService, remoteInterfaceProvider, executorService);
    }

    private RemoteSystemProcessingResource getRemoteSystemProcessingResource(String nodeId) throws NodeNotFoundException {
        final Node targetNode = nodeService.byNodeId(nodeId);

        return remoteInterfaceProvider.get(targetNode,
                this.authenticationToken,
                RemoteSystemProcessingResource.class);
    }

    @POST
    @Timed
    @ApiOperation(value = "Pause message processing on node",
            notes = "If the message journal is enabled, incoming messages will be spooled on disk, if it is disabled, " +
                    "you might lose messages from inputs which cannot buffer themselves, like AMQP or Kafka-based inputs.")
    @Path("pause")
    @NoAuditEvent("proxy resource, audit event will be emitted on target node")
    public void pause(@ApiParam(name = "nodeId", value = "The id of the node where processing will be paused.", required = true)
                      @PathParam("nodeId") String nodeId) throws IOException, NodeNotFoundException {
        final Response response = this.getRemoteSystemProcessingResource(nodeId).pause().execute();
        if (!response.isSuccessful()) {
            LOG.warn("Unable to pause message processing on node {}: {}", nodeId, response.message());
            throw new WebApplicationException(response.message(), BAD_GATEWAY);
        }
    }

    @POST
    @Timed
    @ApiOperation(value = "Resume message processing on node")
    @Path("resume")
    @NoAuditEvent("proxy resource, audit event will be emitted on target node")
    public void resume(@ApiParam(name = "nodeId", value = "The id of the node where processing will be resumed.", required = true)
                       @PathParam("nodeId") String nodeId) throws IOException, NodeNotFoundException {
        final Response response = this.getRemoteSystemProcessingResource(nodeId).resume().execute();
        if (!response.isSuccessful()) {
            LOG.warn("Unable to resume message processing on node {}: {}", nodeId, response.message());
            throw new WebApplicationException(response.message(), BAD_GATEWAY);
        }
    }
}

