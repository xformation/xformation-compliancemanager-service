/*
 * */
package com.synectiks.process.server.rest.resources.system.indexer;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.indexer.cluster.Cluster;
import com.synectiks.process.server.rest.models.system.indexer.responses.ClusterHealth;
import com.synectiks.process.server.rest.models.system.indexer.responses.ClusterName;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RequiresAuthentication
@Api(value = "Indexer/Cluster", description = "Indexer cluster information")
@Path("/system/indexer/cluster")
public class IndexerClusterResource extends RestResource {

    @Inject
    private Cluster cluster;

    @GET
    @Timed
    @Path("/name")
    @RequiresPermissions(RestPermissions.INDEXERCLUSTER_READ)
    @ApiOperation(value = "Get the cluster name")
    @Produces(MediaType.APPLICATION_JSON)
    public ClusterName clusterName() {
        final String clusterName = cluster.clusterName()
                .orElseThrow(() -> new InternalServerErrorException("Couldn't read Elasticsearch cluster health"));
        return ClusterName.create(clusterName);
    }

    @GET
    @Timed
    @Path("/health")
    @ApiOperation(value = "Get cluster and shard health overview")
    @RequiresPermissions(RestPermissions.INDEXERCLUSTER_READ)
    @Produces(MediaType.APPLICATION_JSON)
    public ClusterHealth clusterHealth() {
        return cluster.clusterHealthStats()
                .orElseThrow(() -> new InternalServerErrorException("Couldn't read Elasticsearch cluster health"));
    }
}
