/*
 * */
package com.synectiks.process.server.contentpacks.facades;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.contentpacks.exceptions.ContentPackException;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelType;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.constraints.Constraint;
import com.synectiks.process.server.contentpacks.model.constraints.PluginVersionConstraint;
import com.synectiks.process.server.contentpacks.model.entities.Entity;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.EntityExcerpt;
import com.synectiks.process.server.contentpacks.model.entities.EntityV1;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntity;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.OutputEntity;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.outputs.MessageOutput;
import com.synectiks.process.server.plugin.streams.Output;
import com.synectiks.process.server.rest.models.streams.outputs.requests.CreateOutputRequest;
import com.synectiks.process.server.streams.OutputService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static com.synectiks.process.server.contentpacks.model.entities.references.ReferenceMapUtils.toReferenceMap;
import static com.synectiks.process.server.contentpacks.model.entities.references.ReferenceMapUtils.toValueMap;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OutputFacade implements EntityFacade<Output> {
    private static final Logger LOG = LoggerFactory.getLogger(OutputFacade.class);

    public static final ModelType TYPE_V1 = ModelTypes.OUTPUT_V1;

    private final ObjectMapper objectMapper;
    private final OutputService outputService;
    private final Set<PluginMetaData> pluginMetaData;
    private final Map<String, MessageOutput.Factory<? extends MessageOutput>> outputFactories;
    private final Map<String, MessageOutput.Factory2<? extends MessageOutput>> outputFactories2;

    @Inject
    public OutputFacade(ObjectMapper objectMapper,
                        OutputService outputService,
                        Set<PluginMetaData> pluginMetaData,
                        Map<String, MessageOutput.Factory<? extends MessageOutput>> outputFactories,
                        Map<String, MessageOutput.Factory2<? extends MessageOutput>> outputFactories2) {
        this.objectMapper = objectMapper;
        this.outputService = outputService;
        this.pluginMetaData = pluginMetaData;
        this.outputFactories = outputFactories;
        this.outputFactories2 = outputFactories2;
    }

    @VisibleForTesting
    Entity exportNativeEntity(Output output, EntityDescriptorIds entityDescriptorIds) {
        final OutputEntity outputEntity = OutputEntity.create(
                ValueReference.of(output.getTitle()),
                ValueReference.of(output.getType()),
                toReferenceMap(output.getConfiguration())
        );
        final JsonNode data = objectMapper.convertValue(outputEntity, JsonNode.class);
        final Set<Constraint> constraints = versionConstraints(output);
        return EntityV1.builder()
                .id(ModelId.of(entityDescriptorIds.getOrThrow(output.getId(), ModelTypes.OUTPUT_V1)))
                .type(ModelTypes.OUTPUT_V1)
                .constraints(ImmutableSet.copyOf(constraints))
                .data(data)
                .build();

    }

    private Set<Constraint> versionConstraints(Output output) {
        // TODO: Find more robust method of identifying the providing plugin

        // We have two output lists for backwards compatibility.
        // See comments in MessageOutput.Factory and MessageOutput.Factory2 for details
        final MessageOutput.Factory<? extends MessageOutput> outputFactory = outputFactories.get(output.getType());
        final MessageOutput.Factory2<? extends MessageOutput> outputFactory2 = outputFactories2.get(output.getType());
        if (outputFactory == null && outputFactory2 == null) {
            throw new ContentPackException("Unknown output type: " + output.getType());
        }
        // We have to use the descriptor because the factory is only a runtime-generated proxy. :(
        final String packageName;
        if (outputFactory2 != null) {
            packageName = outputFactory2.getDescriptor().getClass().getPackage().getName();
        } else {
            packageName = outputFactory.getDescriptor().getClass().getPackage().getName();
        }
        return pluginMetaData.stream()
                .filter(metaData -> packageName.startsWith(metaData.getClass().getPackage().getName()))
                .map(PluginVersionConstraint::of)
                .collect(Collectors.toSet());
    }

    @Override
    public NativeEntity<Output> createNativeEntity(Entity entity,
                                                   Map<String, ValueReference> parameters,
                                                   Map<EntityDescriptor, Object> nativeEntities,
                                                   String username) {
        if (entity instanceof EntityV1) {
            return decode((EntityV1) entity, parameters, username);
        } else {
            throw new IllegalArgumentException("Unsupported entity version: " + entity.getClass());

        }
    }

    private NativeEntity<Output> decode(EntityV1 entity, Map<String, ValueReference> parameters, String username) {
        final OutputEntity outputEntity = objectMapper.convertValue(entity.data(), OutputEntity.class);
        final CreateOutputRequest createOutputRequest = CreateOutputRequest.create(
                outputEntity.title().asString(parameters),
                outputEntity.type().asString(parameters),
                toValueMap(outputEntity.configuration(), parameters),
                null // Outputs are assigned to streams in StreamFacade
        );
        try {
            final Output output = outputService.create(createOutputRequest, username);
            return NativeEntity.create(entity.id(), output.getId(), TYPE_V1, output.getTitle(), output);
        } catch (ValidationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Optional<NativeEntity<Output>> loadNativeEntity(NativeEntityDescriptor nativeEntityDescriptor) {
        try {
            final Output output = outputService.load(nativeEntityDescriptor.id().id());
            return Optional.of(NativeEntity.create(nativeEntityDescriptor, output));
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(Output nativeEntity) {
        try {
            outputService.destroy(nativeEntity);
        } catch (NotFoundException ignore) {
        }
    }

    @Override
    public EntityExcerpt createExcerpt(Output output) {
        return EntityExcerpt.builder()
                .id(ModelId.of(output.getId()))
                .type(ModelTypes.OUTPUT_V1)
                .title(output.getTitle())
                .build();
    }

    @Override
    public Set<EntityExcerpt> listEntityExcerpts() {
        return outputService.loadAll().stream()
                .map(this::createExcerpt)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<Entity> exportEntity(EntityDescriptor entityDescriptor, EntityDescriptorIds entityDescriptorIds) {
        final ModelId modelId = entityDescriptor.id();
        try {
            final Output output = outputService.load(modelId.id());
            return Optional.of(exportNativeEntity(output, entityDescriptorIds));
        } catch (NotFoundException e) {
            LOG.debug("Couldn't find output {}", entityDescriptor, e);
            return Optional.empty();
        }
    }
}
