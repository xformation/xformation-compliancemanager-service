/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.rest;

import com.google.common.base.Strings;
import com.synectiks.process.common.plugins.pipelineprocessor.processors.ConfigurationStateUpdater;
import com.synectiks.process.common.plugins.pipelineprocessor.processors.PipelineInterpreter;
import com.synectiks.process.common.plugins.pipelineprocessor.simulator.PipelineInterpreterTracer;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.rest.PluginRestResource;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.rest.models.messages.responses.ResultMessageSummary;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;
import com.synectiks.process.server.streams.StreamService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Api(value = "Pipelines/Simulator", description = "Simulate pipeline message processor")
@Path("/system/pipelines/simulate")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiresAuthentication
public class SimulatorResource extends RestResource implements PluginRestResource {
    private final ConfigurationStateUpdater pipelineStateUpdater;
    private final StreamService streamService;
    private final PipelineInterpreter pipelineInterpreter;

    @Inject
    public SimulatorResource(PipelineInterpreter pipelineInterpreter,
                             ConfigurationStateUpdater pipelineStateUpdater,
                             StreamService streamService) {
        this.pipelineInterpreter = pipelineInterpreter;
        this.pipelineStateUpdater = pipelineStateUpdater;
        this.streamService = streamService;
    }

    @ApiOperation(value = "Simulate the execution of the pipeline message processor")
    @POST
    @RequiresPermissions(PipelineRestPermissions.PIPELINE_RULE_READ)
    @NoAuditEvent("only used to test pipelines, no changes made in the system")
    public SimulationResponse simulate(@ApiParam(name = "simulation", required = true) @NotNull SimulationRequest request) throws NotFoundException {
        checkPermission(RestPermissions.STREAMS_READ, request.streamId());

        final Message message = new Message(request.message());
        final Stream stream = streamService.load(request.streamId());
        message.addStream(stream);

        if (!Strings.isNullOrEmpty(request.inputId())) {
            message.setSourceInputId(request.inputId());
        }

        final List<ResultMessageSummary> simulationResults = new ArrayList<>();
        final PipelineInterpreterTracer pipelineInterpreterTracer = new PipelineInterpreterTracer();

        com.synectiks.process.server.plugin.Messages processedMessages = pipelineInterpreter.process(message,
                                                                                     pipelineInterpreterTracer.getSimulatorInterpreterListener(),
                                                                                     pipelineStateUpdater.getLatestState());
        for (Message processedMessage : processedMessages) {
            simulationResults.add(ResultMessageSummary.create(null, processedMessage.getFields(), ""));
        }

        return SimulationResponse.create(simulationResults,
                                         pipelineInterpreterTracer.getExecutionTrace(),
                                         pipelineInterpreterTracer.took());
    }
}
