/*
 * */
package com.synectiks.process.server.rest.resources.system;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.system.stats.StatsService;
import com.synectiks.process.server.shared.system.stats.SystemStats;
import com.synectiks.process.server.shared.system.stats.fs.FsStats;
import com.synectiks.process.server.shared.system.stats.jvm.JvmStats;
import com.synectiks.process.server.shared.system.stats.network.NetworkStats;
import com.synectiks.process.server.shared.system.stats.os.OsStats;
import com.synectiks.process.server.shared.system.stats.process.ProcessStats;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(value = "System/Stats", description = "Node system stats")
@Path("/system/stats")
@Produces(MediaType.APPLICATION_JSON)
@RequiresAuthentication
public class StatsResource extends RestResource {
    private final StatsService statsService;

    @Inject
    public StatsResource(StatsService statsService) {
        this.statsService = statsService;
    }

    @GET
    @Timed
    @ApiOperation(value = "System information about this node.",
            notes = "This resource returns information about the system this node is running on.")
    public SystemStats systemStats() {
        return statsService.systemStats();
    }

    @GET
    @Path("/fs")
    @Timed
    @ApiOperation(value = "Filesystem information about this node.",
            notes = "This resource returns information about the filesystems of this node.")
    public FsStats fsStats() {
        return statsService.fsStats();
    }

    @GET
    @Path("/jvm")
    @Timed
    @ApiOperation(value = "JVM information about this node.",
            notes = "This resource returns information about the Java Virtual Machine of this node.")
    public JvmStats jvmStats() {
        return statsService.jvmStats();
    }

    @GET
    @Path("/network")
    @Timed
    @ApiOperation(value = "Networking information about this node.",
            notes = "This resource returns information about the networking system this node is running with.")
    public NetworkStats networkStats() {
        return statsService.networkStats();
    }

    @GET
    @Path("/os")
    @Timed
    @ApiOperation(value = "OS information about this node.",
            notes = "This resource returns information about the operating system this node is running on.")
    public OsStats osStats() {
        return statsService.osStats();
    }

    @GET
    @Path("/process")
    @Timed
    @ApiOperation(value = "Process information about this node.",
            notes = "This resource returns information about the process this node is running as.")
    public ProcessStats processStats() {
        return statsService.processStats();
    }
}
