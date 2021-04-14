/*
 * */
package com.synectiks.process.server.shared.rest.resources.system;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Iterables;
import com.synectiks.process.server.plugin.GlobalMetricNames;
import com.synectiks.process.server.rest.models.system.responses.Throughput;
import com.synectiks.process.server.shared.metrics.MetricUtils;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.SortedMap;

@RequiresAuthentication
@Api(value = "System/Throughput", description = "Message throughput of this node")
@Path("/system/throughput")
public class ThroughputResource extends RestResource {
    private final MetricRegistry metricRegistry;

    @Inject
    public ThroughputResource(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @GET
    @Timed
    @RequiresPermissions(RestPermissions.THROUGHPUT_READ)
    @ApiOperation(value = "Current throughput of this node in messages per second")
    @Produces(MediaType.APPLICATION_JSON)
    public Throughput total() {
        final SortedMap<String, Gauge> gauges = metricRegistry.getGauges(MetricUtils.filterSingleMetric(
                GlobalMetricNames.OUTPUT_THROUGHPUT_RATE));
        final Gauge gauge = Iterables.getOnlyElement(gauges.values(), null);
        if (gauge == null || !(gauge.getValue() instanceof Number)) {
            return Throughput.create(0);
        } else {
            return Throughput.create(((Number) gauge.getValue()).longValue());
        }
    }
}
