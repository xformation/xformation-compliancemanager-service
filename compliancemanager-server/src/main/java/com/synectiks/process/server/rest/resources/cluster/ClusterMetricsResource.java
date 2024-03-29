/*
 * */
package com.synectiks.process.server.rest.resources.cluster;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.cluster.NodeNotFoundException;
import com.synectiks.process.server.cluster.NodeService;
import com.synectiks.process.server.rest.RemoteInterfaceProvider;
import com.synectiks.process.server.rest.models.system.metrics.requests.MetricsReadRequest;
import com.synectiks.process.server.rest.models.system.metrics.responses.MetricsSummaryResponse;
import com.synectiks.process.server.shared.rest.resources.ProxiedResource;
import com.synectiks.process.server.shared.rest.resources.system.RemoteMetricsResource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

@RequiresAuthentication
@Api(value = "Cluster/Metrics", description = "Cluster-wide Internal compliancemanager metrics")
@Path("/cluster/metrics")
@Produces(MediaType.APPLICATION_JSON)
public class ClusterMetricsResource extends ProxiedResource {

    @Inject
    public ClusterMetricsResource(NodeService nodeService,
                                  RemoteInterfaceProvider remoteInterfaceProvider,
                                  @Context HttpHeaders httpHeaders,
                                  @Named("proxiedRequestsExecutorService") ExecutorService executorService) {
        super(httpHeaders, nodeService, remoteInterfaceProvider, executorService);
    }

    @POST
    @Timed
    @Path("/multiple")
    @ApiOperation(value = "Get all metrics of all nodes in the cluster")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Malformed body")
    })
    @NoAuditEvent("only used to retrieve metrics of all nodes")
    public Map<String, Optional<MetricsSummaryResponse>> multipleMetricsAllNodes(@ApiParam(name = "Requested metrics", required = true)
                                                                                 @Valid @NotNull MetricsReadRequest request) throws IOException, NodeNotFoundException {
        return getForAllNodes(remoteMetricsResource -> remoteMetricsResource.multipleMetrics(request), createRemoteInterfaceProvider(RemoteMetricsResource.class));
    }
}
