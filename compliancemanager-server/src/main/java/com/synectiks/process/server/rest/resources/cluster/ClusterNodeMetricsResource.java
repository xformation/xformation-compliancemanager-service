/*
 * */
package com.synectiks.process.server.rest.resources.cluster;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.cluster.Node;
import com.synectiks.process.server.cluster.NodeNotFoundException;
import com.synectiks.process.server.cluster.NodeService;
import com.synectiks.process.server.rest.RemoteInterfaceProvider;
import com.synectiks.process.server.rest.models.system.metrics.requests.MetricsReadRequest;
import com.synectiks.process.server.rest.models.system.metrics.responses.MetricNamesResponse;
import com.synectiks.process.server.rest.models.system.metrics.responses.MetricsSummaryResponse;
import com.synectiks.process.server.shared.rest.resources.ProxiedResource;
import com.synectiks.process.server.shared.rest.resources.system.RemoteMetricsResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
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
@Api(value = "Cluster/Node/Metrics", description = "Cluster-wide Internal compliancemanager node metrics")
@Path("/cluster/{nodeId}/metrics")
@Produces(MediaType.APPLICATION_JSON)
public class ClusterNodeMetricsResource extends ProxiedResource {
    @Inject
    public ClusterNodeMetricsResource(NodeService nodeService,
                                      RemoteInterfaceProvider remoteInterfaceProvider,
                                      @Context HttpHeaders httpHeaders,
                                      @Named("proxiedRequestsExecutorService") ExecutorService executorService) {
        super(httpHeaders, nodeService, remoteInterfaceProvider, executorService);
    }

    private RemoteMetricsResource getResourceForNode(String nodeId) throws NodeNotFoundException {
        final Node targetNode = nodeService.byNodeId(nodeId);
        return remoteInterfaceProvider.get(targetNode, this.authenticationToken, RemoteMetricsResource.class);
    }

    @GET
    @Timed
    @Path("/names")
    @ApiOperation(value = "Get all metrics keys/names from node")
    @RequiresPermissions(RestPermissions.METRICS_ALLKEYS)
    public MetricNamesResponse metricNames(@ApiParam(name = "nodeId", value = "The id of the node whose metrics we want.", required = true)
                                           @PathParam("nodeId") String nodeId) throws IOException, NodeNotFoundException {
        final Response<MetricNamesResponse> result = getResourceForNode(nodeId).metricNames().execute();
        if (result.isSuccessful()) {
            return result.body();
        } else {
            throw new WebApplicationException(result.message(), BAD_GATEWAY);
        }
    }

    @POST
    @Timed
    @Path("/multiple")
    @ApiOperation("Get the values of multiple metrics at once from node")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Malformed body")
    })
    @NoAuditEvent("only used to get multiple metric values")
    public MetricsSummaryResponse multipleMetrics(@ApiParam(name = "nodeId", value = "The id of the node whose metrics we want.", required = true)
                                                  @PathParam("nodeId") String nodeId,
                                                  @ApiParam(name = "Requested metrics", required = true)
                                                  @Valid @NotNull MetricsReadRequest request) throws IOException, NodeNotFoundException {
        final Response<MetricsSummaryResponse> result = getResourceForNode(nodeId).multipleMetrics(request).execute();
        if (result.isSuccessful()) {
            return result.body();
        } else {
            throw new WebApplicationException(result.message(), BAD_GATEWAY);
        }
    }

    @GET
    @Timed
    @Path("/namespace/{namespace}")
    @ApiOperation(value = "Get all metrics of a namespace from node")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No such metric namespace")
    })
    public MetricsSummaryResponse byNamespace(@ApiParam(name = "nodeId", value = "The id of the node whose metrics we want.", required = true)
                                              @PathParam("nodeId") String nodeId,
                                              @ApiParam(name = "namespace", required = true)
                                              @PathParam("namespace") String namespace) throws IOException, NodeNotFoundException {
        final Response<MetricsSummaryResponse> result = getResourceForNode(nodeId).byNamespace(namespace).execute();
        if (result.isSuccessful()) {
            return result.body();
        } else {
            throw new WebApplicationException(result.message(), BAD_GATEWAY);
        }
    }
}
