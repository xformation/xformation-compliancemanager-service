/*
 * */
package com.synectiks.process.server.rest.resources.system.inputs;

import com.codahale.metrics.annotation.Timed;
import com.google.common.eventbus.EventBus;
import com.synectiks.process.server.audit.AuditEventTypes;
import com.synectiks.process.server.audit.jersey.AuditEvent;
import com.synectiks.process.server.inputs.InputService;
import com.synectiks.process.server.plugin.IOState;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputCreated;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputDeleted;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputStateSummary;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputStatesList;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputSummary;
import com.synectiks.process.server.shared.inputs.InputRegistry;
import com.synectiks.process.server.shared.inputs.MessageInputFactory;
import com.synectiks.process.server.shared.security.RestPermissions;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Set;
import java.util.stream.Collectors;

@RequiresAuthentication
@Api(value = "System/InputStates", description = "Message input states of this node")
@Path("/system/inputstates")
@Produces(MediaType.APPLICATION_JSON)
public class InputStatesResource extends AbstractInputsResource {
    private final InputRegistry inputRegistry;
    private final EventBus serverEventBus;
    private final InputService inputService;

    @Inject
    public InputStatesResource(InputRegistry inputRegistry,
                               EventBus serverEventBus,
                               InputService inputService,
                               MessageInputFactory messageInputFactory) {
        super(messageInputFactory.getAvailableInputs());
        this.inputRegistry = inputRegistry;
        this.serverEventBus = serverEventBus;
        this.inputService = inputService;
    }

    @GET
    @Timed
    @ApiOperation(value = "Get all input states of this node")
    public InputStatesList list() {
        final Set<InputStateSummary> result = this.inputRegistry.stream()
                .filter(inputState -> isPermitted(RestPermissions.INPUTS_READ, inputState.getStoppable().getId()))
                .map(this::getInputStateSummary)
                .collect(Collectors.toSet());

        return InputStatesList.create(result);
    }

    @GET
    @Path("/{inputId}")
    @Timed
    @ApiOperation(value = "Get input state for specified input id on this node")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No such input on this node."),
    })
    public InputStateSummary get(@ApiParam(name = "inputId", required = true) @PathParam("inputId") String inputId) {
        checkPermission(RestPermissions.INPUTS_READ, inputId);
        final IOState<MessageInput> inputState = this.inputRegistry.getInputState(inputId);
        if (inputState == null) {
            throw new NotFoundException("No input state for input id <" + inputId + "> on this node.");
        }
        return getInputStateSummary(inputState);
    }

    @PUT
    @Path("/{inputId}")
    @Timed
    @ApiOperation(value = "(Re-)Start specified input on this node")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No such input on this node."),
    })
    @AuditEvent(type = AuditEventTypes.MESSAGE_INPUT_START)
    public InputCreated start(@ApiParam(name = "inputId", required = true) @PathParam("inputId") String inputId) throws com.synectiks.process.server.database.NotFoundException {
        checkPermission(RestPermissions.INPUTS_CHANGESTATE, inputId);
        inputService.find(inputId);
        final InputCreated result = InputCreated.create(inputId);
        this.serverEventBus.post(result);

        return result;
    }

    @DELETE
    @Path("/{inputId}")
    @Timed
    @ApiOperation(value = "Stop specified input on this node")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No such input on this node."),
    })
    @AuditEvent(type = AuditEventTypes.MESSAGE_INPUT_STOP)
    public InputDeleted stop(@ApiParam(name = "inputId", required = true) @PathParam("inputId") String inputId) throws com.synectiks.process.server.database.NotFoundException {
        checkPermission(RestPermissions.INPUTS_CHANGESTATE, inputId);
        inputService.find(inputId);
        final InputDeleted result = InputDeleted.create(inputId);
        this.serverEventBus.post(result);

        return result;
    }

    private InputStateSummary getInputStateSummary(IOState<MessageInput> inputState) {
        final MessageInput messageInput = inputState.getStoppable();
        return InputStateSummary.create(
                messageInput.getId(),
                inputState.getState().toString(),
                inputState.getStartedAt(),
                inputState.getDetailedMessage(),
                InputSummary.create(
                        messageInput.getTitle(),
                        messageInput.isGlobal(),
                        messageInput.getName(),
                        messageInput.getContentPack(),
                        messageInput.getId(),
                        messageInput.getCreatedAt(),
                        messageInput.getType(),
                        messageInput.getCreatorUserId(),
                        // Ensure password masking!
                        maskPasswordsInConfiguration(
                                messageInput.getConfiguration().getSource(),
                                messageInput.getRequestedConfiguration()
                        ),
                        messageInput.getStaticFields(),
                        messageInput.getNodeId()
                )
        );
    }
}
