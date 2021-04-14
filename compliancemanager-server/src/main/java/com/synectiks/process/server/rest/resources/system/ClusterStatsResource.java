/*
 * */
package com.synectiks.process.server.rest.resources.system;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.system.stats.ClusterStats;
import com.synectiks.process.server.system.stats.ClusterStatsService;
import com.synectiks.process.server.system.stats.elasticsearch.ElasticsearchStats;
import com.synectiks.process.server.system.stats.mongo.MongoStats;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(value = "System/ClusterStats", description = "[DEPRECATED] Cluster stats")
@RequiresAuthentication
@Path("/system/cluster/stats")
@Produces(MediaType.APPLICATION_JSON)
@Deprecated
public class ClusterStatsResource extends RestResource {
    private final ClusterStatsService clusterStatsService;

    @Inject
    public ClusterStatsResource(ClusterStatsService clusterStatsService) {
        this.clusterStatsService = clusterStatsService;
    }

    @GET
    @Timed
    @ApiOperation(value = "Cluster status information.",
            notes = "This resource returns information about the alertmanager cluster.")
    public ClusterStats systemStats() {
        return clusterStatsService.clusterStats();
    }

    @GET
    @Path("/elasticsearch")
    @Timed
    @ApiOperation(value = "Elasticsearch information.",
            notes = "This resource returns information about the Elasticsearch Cluster.")
    public ElasticsearchStats elasticsearchStats() {
        return clusterStatsService.elasticsearchStats();
    }

    @GET
    @Path("/mongo")
    @Timed
    @ApiOperation(value = "MongoDB information.",
            notes = "This resource returns information about MongoDB.")
    public MongoStats mongoStats() {
        return clusterStatsService.mongoStats();
    }

}
