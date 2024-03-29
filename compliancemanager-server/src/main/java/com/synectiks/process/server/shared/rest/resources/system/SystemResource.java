/*
 * */
package com.synectiks.process.server.shared.rest.resources.system;

import com.codahale.metrics.annotation.Timed;
import com.codahale.metrics.jvm.ThreadDump;
import com.synectiks.process.server.uuid.UUID;
import com.github.joschi.jadconfig.util.Size;
import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.cluster.ClusterId;
import com.synectiks.process.server.rest.models.system.responses.LocalesResponse;
import com.synectiks.process.server.rest.models.system.responses.SystemJVMResponse;
import com.synectiks.process.server.rest.models.system.responses.SystemOverviewResponse;
import com.synectiks.process.server.rest.models.system.responses.SystemProcessBufferDumpResponse;
import com.synectiks.process.server.rest.models.system.responses.SystemThreadDumpResponse;
import com.synectiks.process.server.shared.ServerVersion;
import com.synectiks.process.server.shared.buffers.ProcessBuffer;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

@RequiresAuthentication
@Api(value = "System", description = "System information of this node.")
@Path("/system")
@Produces(MediaType.APPLICATION_JSON)
public class SystemResource extends RestResource {
    private final ServerStatus serverStatus;
    private final ClusterId clusterId;
    private final ProcessBuffer processBuffer;

    @Inject
    public SystemResource(ServerStatus serverStatus, ClusterConfigService clusterConfigService, ProcessBuffer processBuffer) {
        this.serverStatus = serverStatus;
        this.clusterId = clusterConfigService.getOrDefault(ClusterId.class, ClusterId.create(UUID.nilUUID().toString()));
        this.processBuffer = processBuffer;
    }

    @GET
    @Timed
    @ApiOperation(value = "Get system overview")
    public SystemOverviewResponse system() {
        checkPermission(RestPermissions.SYSTEM_READ, serverStatus.getNodeId().toString());


        return SystemOverviewResponse.create("compliancemanager-server",
                ServerVersion.CODENAME,
                serverStatus.getNodeId().toString(),
                clusterId.clusterId(),
                ServerVersion.VERSION.toString(),
                Tools.getISO8601String(serverStatus.getStartedAt()),
                serverStatus.isProcessing(),
                Tools.getLocalCanonicalHostname(),
                serverStatus.getLifecycle().getDescription().toLowerCase(Locale.ENGLISH),
                serverStatus.getLifecycle().getLoadbalancerStatus().toString().toLowerCase(Locale.ENGLISH),
                serverStatus.getTimezone().getID(),
                System.getProperty("os.name", "unknown") + " " + System.getProperty("os.version", "unknown"));
    }

    @GET
    @ApiOperation(value = "Get JVM information")
    @Path("/jvm")
    @Timed
    public SystemJVMResponse jvm() {
        checkPermission(RestPermissions.JVMSTATS_READ, serverStatus.getNodeId().toString());

        Runtime runtime = Runtime.getRuntime();
        return SystemJVMResponse.create(
                bytesToValueMap(runtime.freeMemory()),
                bytesToValueMap(runtime.maxMemory()),
                bytesToValueMap(runtime.totalMemory()),
                bytesToValueMap(runtime.totalMemory() - runtime.freeMemory()),
                serverStatus.getNodeId().toString(),
                Tools.getPID(),
                Tools.getSystemInformation());
    }

    @GET
    @Timed
    @ApiOperation(value = "Get a thread dump")
    @Path("/threaddump")
    public SystemThreadDumpResponse threaddump() {
        checkPermission(RestPermissions.THREADS_DUMP, serverStatus.getNodeId().toString());

        // The ThreadDump is built by internal codahale.metrics servlet library we are abusing.
        final ThreadDump threadDump = new ThreadDump(ManagementFactory.getThreadMXBean());
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        threadDump.dump(output);
        return SystemThreadDumpResponse.create(new String(output.toByteArray(), StandardCharsets.UTF_8));
    }

    @GET
    @Path("/processbufferdump")
    @Timed
    @ApiOperation(value = "Get a process buffer dump")
    public SystemProcessBufferDumpResponse processBufferDump() {
        checkPermission(RestPermissions.PROCESSBUFFER_DUMP, serverStatus.getNodeId().toString());
       return SystemProcessBufferDumpResponse.create(processBuffer.getDump());
    }

    @GET
    @Path("/threaddump")
    @Produces(MediaType.TEXT_PLAIN)
    @Timed
    @ApiOperation(value = "Get a thread dump as plain text")
    public StreamingOutput threadDumpAsText() {
        checkPermission(RestPermissions.THREADS_DUMP, serverStatus.getNodeId().toString());
        return output -> new ThreadDump(ManagementFactory.getThreadMXBean()).dump(output);
    }

    @GET
    @ApiOperation(value = "Get supported locales")
    @Path("/locales")
    @Timed
    public LocalesResponse locales() {
        return LocalesResponse.create(Locale.getAvailableLocales());
    }

    private Map<String, Long> bytesToValueMap(long bytes) {
        final Size size = Size.bytes(bytes);
        return ImmutableMap.of(
                "bytes", size.toBytes(),
                "kilobytes", size.toKilobytes(),
                "megabytes", size.toMegabytes());
    }
}
