/*
 * */
package com.synectiks.process.server.rest.resources.system.processing;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.cluster.NodeService;
import com.synectiks.process.server.rest.RemoteInterfaceProvider;
import com.synectiks.process.server.rest.models.system.processing.ProcessingStatusSummary;
import com.synectiks.process.server.shared.rest.resources.ProxiedResource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

@RequiresAuthentication
@Api(value = "Cluster/Processing/Status")
@Path("/cluster/processing/status")
@Produces(MediaType.APPLICATION_JSON)
public class ClusterProcessingStatusResource extends ProxiedResource {
    @Inject
    public ClusterProcessingStatusResource(NodeService nodeService,
                                           RemoteInterfaceProvider remoteInterfaceProvider,
                                           @Context HttpHeaders httpHeaders,
                                           @Named("proxiedRequestsExecutorService") ExecutorService executorService) {
        super(httpHeaders, nodeService, remoteInterfaceProvider, executorService);
    }

    @GET
    @Timed
    @ApiOperation(value = "Get processing status from all nodes in the cluster")
    public Map<String, Optional<ProcessingStatusSummary>> getStatus() {
        return getForAllNodes(RemoteSystemProcessingStatusResource::getStatus, createRemoteInterfaceProvider(RemoteSystemProcessingStatusResource.class));
    }

    @GET
    @Path("/persisted")
    @Timed
    @ApiOperation(value = "Get persisted processing status from all nodes in the cluster")
    public Map<String, Optional<ProcessingStatusSummary>> getPersistedStatus() {
        return getForAllNodes(RemoteSystemProcessingStatusResource::getPersistedStatus, createRemoteInterfaceProvider(RemoteSystemProcessingStatusResource.class));
    }
}
