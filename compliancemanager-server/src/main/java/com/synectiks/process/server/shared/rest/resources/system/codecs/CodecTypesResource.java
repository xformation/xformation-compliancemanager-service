/*
 * */
package com.synectiks.process.server.shared.rest.resources.system.codecs;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.inputs.codecs.CodecFactory;
import com.synectiks.process.server.plugin.inputs.codecs.Codec;
import com.synectiks.process.server.rest.models.system.codecs.responses.CodecTypeInfo;
import com.synectiks.process.server.shared.rest.resources.RestResource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.stream.Collectors;

@RequiresAuthentication
@Api(value = "System/Codecs/Types", description = "Message codec types of this node")
@Path("/system/codecs/types")
@Produces(MediaType.APPLICATION_JSON)
public class CodecTypesResource extends RestResource {
    private CodecFactory codecFactory;

    @Inject
    public CodecTypesResource(CodecFactory codecFactory) {
        this.codecFactory = codecFactory;
    }

    @GET
    @Timed
    @Path("/all")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get all codec types")
    public Map<String, CodecTypeInfo> getAll() {
        final Map<String, Codec.Factory<? extends Codec>> factories = codecFactory.getFactory();

        return factories
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            final Codec.Factory<? extends Codec> factory = entry.getValue();
                            return CodecTypeInfo.fromConfigurationRequest(entry.getKey(), factory.getDescriptor().getName(), factory.getConfig().getRequestedConfiguration());
                        }
                ));
    }
}
