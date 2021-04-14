/*
 * */
package com.synectiks.process.common.events.rest;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.events.fields.providers.FieldValueProvider;
import com.synectiks.process.common.events.processor.EventProcessor;
import com.synectiks.process.common.events.processor.aggregation.AggregationFunction;
import com.synectiks.process.common.events.processor.storage.EventStorageHandler;
import com.synectiks.process.server.plugin.rest.PluginRestResource;
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
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Api(value = "Events/EntityTypes", description = "Event entity types")
@Path("/events/entity_types")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiresAuthentication
public class AvailableEntityTypesResource extends RestResource implements PluginRestResource {
    private final Set<String> eventProcessorTypes;
    private final Set<String> fieldValueProviderTypes;
    private final Set<String> storageHandlerFactories;
    private final Set<String> aggregationFunctions;

    @Inject
    public AvailableEntityTypesResource(Map<String, EventProcessor.Factory> eventProcessorFactories,
                                        Map<String, FieldValueProvider.Factory> fieldValueProviders,
                                        Map<String, EventStorageHandler.Factory> storageHandlerFactories) {
        this.eventProcessorTypes = eventProcessorFactories.keySet();
        this.fieldValueProviderTypes = fieldValueProviders.keySet();
        this.storageHandlerFactories = storageHandlerFactories.keySet();
        this.aggregationFunctions = Arrays.stream(AggregationFunction.values())
                .map(fn -> fn.name().toLowerCase(Locale.US))
                .collect(Collectors.toSet());
    }

    @GET
    @ApiOperation("List all available entity types")
    public AvailableEntityTypesSummary all() {
        return AvailableEntityTypesSummary.create(eventProcessorTypes, fieldValueProviderTypes, storageHandlerFactories, aggregationFunctions);
    }

    @AutoValue
    @JsonAutoDetect
    public static abstract class AvailableEntityTypesSummary {
        @JsonProperty("processor_types")
        public abstract Set<String> processorTypes();

        @JsonProperty("field_provider_types")
        public abstract Set<String> fieldProviderTypes();

        @JsonProperty("storage_handler_types")
        public abstract Set<String> storageHandlerTypes();

        @JsonProperty("aggregation_functions")
        public abstract Set<String> aggregationFunctions();

        public static AvailableEntityTypesSummary create(Set<String> processorTypes,
                                                         Set<String> fieldProviderTypes,
                                                         Set<String> storageHandlerTypes,
                                                         Set<String> aggregationFunctions) {
            return new AutoValue_AvailableEntityTypesResource_AvailableEntityTypesSummary(processorTypes, fieldProviderTypes, storageHandlerTypes, aggregationFunctions);
        }
    }
}
