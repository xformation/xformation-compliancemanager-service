/*
 * */
package com.synectiks.process.server.rest.resources.system;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.joda.time.Duration;

import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.system.traffic.TrafficCounterService;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Api(value = "System/ClusterTraffic", description = "Cluster traffic stats")
@RequiresAuthentication
@Path("/system/cluster/traffic")
@Produces(MediaType.APPLICATION_JSON)
public class TrafficResource extends RestResource {

    private final TrafficCounterService trafficCounterService;

    @Inject
    public TrafficResource(TrafficCounterService trafficCounterService) {
        this.trafficCounterService = trafficCounterService;
    }

    @GET
    @ApiOperation(value = "Get the cluster traffic stats")
    public TrafficCounterService.TrafficHistogram get(@ApiParam(name = "days", value = "For how many days the traffic stats should be returned")
                                                      @QueryParam("days") @DefaultValue("30") int days,
                                                      @ApiParam(name = "daily", value = "Whether the traffic should be aggregate to daily values")
                                                      @QueryParam("daily") @DefaultValue("false") boolean daily) {
        final TrafficCounterService.TrafficHistogram trafficHistogram =
                trafficCounterService.clusterTrafficOfLastDays(Duration.standardDays(days),
                        daily ? TrafficCounterService.Interval.DAILY : TrafficCounterService.Interval.HOURLY);

        return trafficHistogram;
    }
}
