/*
 * */
package com.synectiks.process.server.rest.resources.system;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.audit.AuditEventTypes;
import com.synectiks.process.server.audit.jersey.AuditEvent;
import com.synectiks.process.server.messageprocessors.MessageProcessorsConfig;
import com.synectiks.process.server.messageprocessors.MessageProcessorsConfigWithDescriptors;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.messageprocessors.MessageProcessor;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Set;
import java.util.stream.Collectors;

@RequiresAuthentication
@Api(value = "System/MessageProcessors", description = "Manage message processors")
@Path("/system/messageprocessors")
@Produces(MediaType.APPLICATION_JSON)
public class MessageProcessorsResource extends RestResource {
    private final Set<String> processorClassNames;
    private final ClusterConfigService clusterConfigService;
    private final Set<MessageProcessor.Descriptor> processorDescriptors;

    @Inject
    public MessageProcessorsResource(final Set<MessageProcessor.Descriptor> processorDescriptors,
                                     final ClusterConfigService clusterConfigService) {
        this.processorDescriptors = processorDescriptors;
        this.processorClassNames = processorDescriptors.stream()
                .map(MessageProcessor.Descriptor::className)
                .collect(Collectors.toSet());
        this.clusterConfigService = clusterConfigService;
    }


    @GET
    @Timed
    @ApiOperation(value = "Get message processor configuration")
    @Path("config")
    public MessageProcessorsConfigWithDescriptors config() {
        checkPermission(RestPermissions.CLUSTER_CONFIG_ENTRY_READ);
        final MessageProcessorsConfig config = clusterConfigService.getOrDefault(MessageProcessorsConfig.class,
                MessageProcessorsConfig.defaultConfig());

        return MessageProcessorsConfigWithDescriptors.fromConfig(config.withProcessors(processorClassNames), processorDescriptors);
    }

    @PUT
    @Timed
    @ApiOperation(value = "Update message processor configuration")
    @Path("config")
    @AuditEvent(type = AuditEventTypes.MESSAGE_PROCESSOR_CONFIGURATION_UPDATE)
    public MessageProcessorsConfigWithDescriptors updateConfig(@ApiParam(name = "config", required = true) final MessageProcessorsConfigWithDescriptors configWithDescriptors) {
        checkPermission(RestPermissions.CLUSTER_CONFIG_ENTRY_EDIT);
        final MessageProcessorsConfig config = configWithDescriptors.toConfig();

        clusterConfigService.write(config.withProcessors(processorClassNames));

        return configWithDescriptors;
    }
}
