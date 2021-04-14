/*
 * */
package com.synectiks.process.server.rest.resources.system.indices;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategy;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategyConfig;
import com.synectiks.process.server.rest.models.system.indices.RotationStrategies;
import com.synectiks.process.server.rest.models.system.indices.RotationStrategyDescription;
import com.synectiks.process.server.shared.rest.resources.RestResource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Api(value = "System/Indices/Rotation", description = "Index rotation strategy settings")
@Path("/system/indices/rotation")
@Produces(MediaType.APPLICATION_JSON)
@RequiresAuthentication
public class RotationStrategyResource extends RestResource {
    private final Map<String, Provider<RotationStrategy>> rotationStrategies;
    private final ObjectMapper objectMapper;
    private final ClusterConfigService clusterConfigService;

    @Inject
    public RotationStrategyResource(Map<String, Provider<RotationStrategy>> rotationStrategies,
                                    ObjectMapper objectMapper,
                                    ClusterConfigService clusterConfigService) {
        this.rotationStrategies = requireNonNull(rotationStrategies);
        this.objectMapper = objectMapper;
        this.clusterConfigService = requireNonNull(clusterConfigService);
    }

    @GET
    @Path("strategies")
    @Timed
    @ApiOperation(value = "List available rotation strategies",
            notes = "This resource returns a list of all available rotation strategies on this Graylog node.")
    public RotationStrategies list() {
        final Set<RotationStrategyDescription> strategies = rotationStrategies.keySet()
                .stream()
                .map(this::getRotationStrategyDescription)
                .collect(Collectors.toSet());

        return RotationStrategies.create(strategies.size(), strategies);
    }

    @GET
    @Path("strategies/{strategy}")
    @Timed
    @ApiOperation(value = "Show JSON schema for configuration of given rotation strategies",
            notes = "This resource returns a JSON schema for the configuration of the given rotation strategy.")
    public RotationStrategyDescription configSchema(@ApiParam(name = "strategy", value = "The name of the rotation strategy", required = true)
                                   @PathParam("strategy") @NotEmpty String strategyName) {
        return getRotationStrategyDescription(strategyName);
    }

    private RotationStrategyDescription getRotationStrategyDescription(String strategyName) {
        final Provider<RotationStrategy> provider = rotationStrategies.get(strategyName);
        if (provider == null) {
            throw new NotFoundException("Couldn't find rotation strategy for given type " + strategyName);
        }

        final RotationStrategy rotationStrategy = provider.get();
        final RotationStrategyConfig defaultConfig = rotationStrategy.defaultConfiguration();
        final SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        try {
            objectMapper.acceptJsonFormatVisitor(objectMapper.constructType(rotationStrategy.configurationClass()), visitor);
        } catch (JsonMappingException e) {
            throw new InternalServerErrorException("Couldn't generate JSON schema for rotation strategy " + strategyName, e);
        }

        return RotationStrategyDescription.create(strategyName, defaultConfig, visitor.finalSchema());
    }
}
