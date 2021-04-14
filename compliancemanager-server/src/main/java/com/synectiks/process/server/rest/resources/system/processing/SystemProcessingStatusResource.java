/*
 * */
package com.synectiks.process.server.rest.resources.system.processing;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.rest.models.system.processing.ProcessingStatusSummary;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.system.processing.DBProcessingStatusService;
import com.synectiks.process.server.system.processing.ProcessingStatusRecorder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(value = "System/Processing/Status")
@Path("/system/processing/status")
@RequiresAuthentication
@Produces(MediaType.APPLICATION_JSON)
public class SystemProcessingStatusResource extends RestResource {
    private final ProcessingStatusRecorder processingStatusRecorder;
    private final DBProcessingStatusService dbService;

    @Inject
    public SystemProcessingStatusResource(ProcessingStatusRecorder processingStatusRecorder,
                                          DBProcessingStatusService dbService) {
        this.processingStatusRecorder = processingStatusRecorder;
        this.dbService = dbService;
    }

    @GET
    @Timed
    @ApiOperation(value = "Get processing status summary from node")
    public ProcessingStatusSummary getStatus() {
        return ProcessingStatusSummary.of(processingStatusRecorder);
    }

    @GET
    @Path("/persisted")
    @Timed
    @ApiOperation(value = "Get persisted processing status summary from node")
    public ProcessingStatusSummary getPersistedStatus() {
        return dbService.get().map(ProcessingStatusSummary::of)
                .orElseThrow(() -> new NotFoundException("No processing status persisted yet"));
    }
}
