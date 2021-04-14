/*
 * */
package com.synectiks.process.server.shared.rest.resources.system.inputs;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputTypeInfo;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputTypesSummary;
import com.synectiks.process.server.shared.inputs.InputDescription;
import com.synectiks.process.server.shared.inputs.MessageInputFactory;
import com.synectiks.process.server.shared.rest.resources.RestResource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RequiresAuthentication
@Api(value = "System/Inputs/Types", description = "Message input types of this node")
@Path("/system/inputs/types")
@Produces(MediaType.APPLICATION_JSON)
public class InputTypesResource extends RestResource {
    private static final Logger LOG = LoggerFactory.getLogger(InputTypesResource.class);
    private final MessageInputFactory messageInputFactory;

    @Inject
    public InputTypesResource(MessageInputFactory messageInputFactory) {
        this.messageInputFactory = messageInputFactory;
    }

    @GET
    @Timed
    @ApiOperation(value = "Get all available input types of this node")
    public InputTypesSummary types() {
        Map<String, String> types = new HashMap<>();
        for (Map.Entry<String, InputDescription> entry : messageInputFactory.getAvailableInputs().entrySet())
            types.put(entry.getKey(), entry.getValue().getName());
        return InputTypesSummary.create(types);
    }

    @GET
    @Timed
    @Path("/all")
    @ApiOperation(value = "Get information about all input types")
    public Map<String, InputTypeInfo> all() {
        final Map<String, InputDescription> availableTypes = messageInputFactory.getAvailableInputs();
        return availableTypes
                .entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> {
                    final InputDescription description = entry.getValue();
                    return InputTypeInfo.create(entry.getKey(), description.getName(), description.isExclusive(),
                            description.getRequestedConfiguration(), description.getLinkToDocs());
                }));
    }

    @GET
    @Timed
    @Path("{inputType}")
    @ApiOperation(value = "Get information about a single input type")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No such input type registered.")
    })
    public InputTypeInfo info(@ApiParam(name = "inputType", required = true) @PathParam("inputType") String inputType) {
        final InputDescription description = messageInputFactory.getAvailableInputs().get(inputType);
        if (description == null) {
            final String message = "Unknown input type " + inputType + " requested.";
            LOG.error(message);
            throw new NotFoundException(message);
        }

        return InputTypeInfo.create(inputType, description.getName(), description.isExclusive(), description.getRequestedConfiguration(), description.getLinkToDocs());
    }
}
