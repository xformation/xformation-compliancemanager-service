/*
 * */
package com.synectiks.process.server.rest.resources.streams.outputs;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.audit.AuditEventTypes;
import com.synectiks.process.server.audit.jersey.AuditEvent;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.outputs.OutputRegistry;
import com.synectiks.process.server.plugin.streams.Output;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.rest.models.streams.outputs.OutputListResponse;
import com.synectiks.process.server.rest.models.streams.outputs.requests.AddOutputRequest;
import com.synectiks.process.server.rest.models.system.outputs.responses.OutputSummary;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;
import com.synectiks.process.server.streams.OutputService;
import com.synectiks.process.server.streams.StreamService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@RequiresAuthentication
@Api(value = "StreamOutputs", description = "Manage stream outputs for a given stream")
@Path("/streams/{streamid}/outputs")
public class StreamOutputResource extends RestResource {
    private final OutputService outputService;
    private final StreamService streamService;
    private final OutputRegistry outputRegistry;

    @Inject
    public StreamOutputResource(OutputService outputService,
                                StreamService streamService,
                                OutputRegistry outputRegistry) {
        this.outputService = outputService;
        this.streamService = streamService;
        this.outputRegistry = outputRegistry;
    }

    @GET
    @Timed
    @ApiOperation(value = "Get a list of all outputs for a stream")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No such stream on this node.")
    })
    public OutputListResponse get(@ApiParam(name = "streamid", value = "The id of the stream whose outputs we want.", required = true)
                                  @PathParam("streamid") String streamid) throws NotFoundException {
        checkPermission(RestPermissions.STREAMS_READ, streamid);
        checkPermission(RestPermissions.STREAM_OUTPUTS_READ);

        final Stream stream = streamService.load(streamid);
        final Set<OutputSummary> outputs = new HashSet<>();

        for (Output output : stream.getOutputs())
            outputs.add(OutputSummary.create(
                    output.getId(),
                    output.getTitle(),
                    output.getType(),
                    output.getCreatorUserId(),
                    new DateTime(output.getCreatedAt()),
                    new HashMap<>(output.getConfiguration()),
                    output.getContentPack()
            ));

        return OutputListResponse.create(outputs);
    }

    @GET
    @Path("/{outputId}")
    @Timed
    @ApiOperation(value = "Get specific output of a stream")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No such stream/output on this node.")
    })
    public OutputSummary get(@ApiParam(name = "streamid", value = "The id of the stream whose outputs we want.", required = true) @PathParam("streamid") String streamid,
                             @ApiParam(name = "outputId", value = "The id of the output we want.", required = true) @PathParam("outputId") String outputId) throws NotFoundException {
        checkPermission(RestPermissions.STREAMS_READ, streamid);
        checkPermission(RestPermissions.STREAM_OUTPUTS_READ, outputId);

        final Output output = outputService.load(outputId);

        return OutputSummary.create(
                output.getId(), output.getTitle(), output.getType(), output.getCreatorUserId(), new DateTime(output.getCreatedAt()), output.getConfiguration(), output.getContentPack()
        );
    }

    @POST
    @Timed
    @ApiOperation(value = "Associate outputs with a stream")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid output specification in input.")
    })
    @AuditEvent(type = AuditEventTypes.STREAM_OUTPUT_ASSIGNMENT_CREATE)
    public Response add(@ApiParam(name = "streamid", value = "The id of the stream whose outputs we want.", required = true)
                        @PathParam("streamid") String streamid,
                        @ApiParam(name = "JSON body", required = true)
                        @Valid @NotNull AddOutputRequest aor) throws NotFoundException {
        checkPermission(RestPermissions.STREAMS_EDIT, streamid);
        checkPermission(RestPermissions.STREAM_OUTPUTS_CREATE);

        // Check if stream exists
        streamService.load(streamid);

        final Set<String> outputs = aor.outputs();
        final ImmutableSet.Builder<ObjectId> outputIds = ImmutableSet.builderWithExpectedSize(outputs.size());
        for (String outputId : outputs) {
            // Check if output exists
            outputService.load(outputId);

            outputIds.add(new ObjectId(outputId));
        }

        streamService.addOutputs(new ObjectId(streamid), outputIds.build());

        return Response.accepted().build();
    }

    @DELETE
    @Path("/{outputId}")
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Delete output of a stream")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No such stream/output on this node.")
    })
    @AuditEvent(type = AuditEventTypes.STREAM_OUTPUT_ASSIGNMENT_DELETE)
    public void remove(@ApiParam(name = "streamid", value = "The id of the stream whose outputs we want.", required = true)
                       @PathParam("streamid") String streamid,
                       @ApiParam(name = "outputId", value = "The id of the output that should be deleted", required = true)
                       @PathParam("outputId") String outputId) throws NotFoundException {
        checkPermission(RestPermissions.STREAMS_EDIT, streamid);
        checkPermission(RestPermissions.STREAM_OUTPUTS_DELETE, outputId);

        final Stream stream = streamService.load(streamid);
        final Output output = outputService.load(outputId);

        streamService.removeOutput(stream, output);
        outputRegistry.removeOutput(output);
    }
}
