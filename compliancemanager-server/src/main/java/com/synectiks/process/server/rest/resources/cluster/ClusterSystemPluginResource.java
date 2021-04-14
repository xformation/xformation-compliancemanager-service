/*
 * */
package com.synectiks.process.server.rest.resources.cluster;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.cluster.Node;
import com.synectiks.process.server.cluster.NodeNotFoundException;
import com.synectiks.process.server.cluster.NodeService;
import com.synectiks.process.server.rest.RemoteInterfaceProvider;
import com.synectiks.process.server.rest.models.system.plugins.responses.PluginList;
import com.synectiks.process.server.shared.rest.resources.ProxiedResource;
import com.synectiks.process.server.shared.rest.resources.system.RemoteSystemPluginResource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
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
@Api(value = "Cluster/Plugins", description = "Plugin information for any node in the cluster")
@Path("/cluster/{nodeId}/plugins")
@Produces(MediaType.APPLICATION_JSON)
public class ClusterSystemPluginResource extends ProxiedResource {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterSystemResource.class);

    @Inject
    public ClusterSystemPluginResource(NodeService nodeService,
                                       RemoteInterfaceProvider remoteInterfaceProvider,
                                       @Context HttpHeaders httpHeaders,
                                       @Named("proxiedRequestsExecutorService") ExecutorService executorService) throws NodeNotFoundException {
        super(httpHeaders, nodeService, remoteInterfaceProvider, executorService);
    }

    @GET
    @Timed
    @ApiOperation(value = "List all installed plugins on the given node")
    public PluginList list(@ApiParam(name = "nodeId", value = "The id of the node where processing will be paused.", required = true)
                           @PathParam("nodeId") String nodeId) throws IOException, NodeNotFoundException {
        final Node targetNode = nodeService.byNodeId(nodeId);

        final RemoteSystemPluginResource remoteSystemPluginResource = remoteInterfaceProvider.get(targetNode,
                this.authenticationToken,
                RemoteSystemPluginResource.class);
        final Response<PluginList> response = remoteSystemPluginResource.list().execute();
        if (response.isSuccessful()) {
            return response.body();
        } else {
            LOG.warn("Unable to get plugin list on node {}: {}", nodeId, response.message());
            throw new WebApplicationException(response.message(), BAD_GATEWAY);
        }
    }
}
