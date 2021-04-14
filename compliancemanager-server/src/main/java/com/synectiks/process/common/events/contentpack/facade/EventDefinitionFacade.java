/*
 * */
package com.synectiks.process.common.events.contentpack.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;
import com.synectiks.process.common.events.contentpack.entities.EventDefinitionEntity;
import com.synectiks.process.common.events.processor.DBEventDefinitionService;
import com.synectiks.process.common.events.processor.EventDefinitionDto;
import com.synectiks.process.common.events.processor.EventDefinitionHandler;
import com.synectiks.process.common.events.processor.EventProcessorExecutionJob;
import com.synectiks.process.common.scheduler.DBJobDefinitionService;
import com.synectiks.process.common.scheduler.JobDefinitionDto;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.contentpacks.facades.EntityFacade;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.constraints.Constraint;
import com.synectiks.process.server.contentpacks.model.constraints.PluginVersionConstraint;
import com.synectiks.process.server.contentpacks.model.entities.Entity;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.EntityExcerpt;
import com.synectiks.process.server.contentpacks.model.entities.EntityV1;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntity;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.shared.users.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class EventDefinitionFacade implements EntityFacade<EventDefinitionDto> {
    private static final Logger LOG = LoggerFactory.getLogger(EventDefinitionFacade.class);

    private final ObjectMapper objectMapper;
    private final EventDefinitionHandler eventDefinitionHandler;
    private final DBJobDefinitionService jobDefinitionService;
    private final DBEventDefinitionService eventDefinitionService;
    private final Set<PluginMetaData> pluginMetaData;
    private final UserService userService;

    @Inject
    public EventDefinitionFacade(ObjectMapper objectMapper,
                                 EventDefinitionHandler eventDefinitionHandler,
                                 Set<PluginMetaData> pluginMetaData,
                                 DBJobDefinitionService jobDefinitionService,
                                 DBEventDefinitionService eventDefinitionService,
                                 UserService userService) {
        this.objectMapper = objectMapper;
        this.pluginMetaData = pluginMetaData;
        this.eventDefinitionHandler = eventDefinitionHandler;
        this.jobDefinitionService = jobDefinitionService;
        this.eventDefinitionService = eventDefinitionService;
        this.userService = userService;
    }

    @VisibleForTesting
    private Entity exportNativeEntity(EventDefinitionDto eventDefinition, EntityDescriptorIds entityDescriptorIds) {
        // Presence of a job definition means that the event definition should be scheduled
        final Optional<JobDefinitionDto> jobDefinition = jobDefinitionService.getByConfigField(EventProcessorExecutionJob.Config.FIELD_EVENT_DEFINITION_ID, eventDefinition.id());

        final EventDefinitionEntity entity = eventDefinition.toContentPackEntity(entityDescriptorIds)
                .toBuilder()
                .isScheduled(ValueReference.of(jobDefinition.isPresent()))
                .build();

        final JsonNode data = objectMapper.convertValue(entity, JsonNode.class);
        return EntityV1.builder()
                .id(ModelId.of(entityDescriptorIds.getOrThrow(eventDefinition.id(), ModelTypes.EVENT_DEFINITION_V1)))
                .type(ModelTypes.EVENT_DEFINITION_V1)
                .constraints(versionConstraints(eventDefinition))
                .data(data)
                .build();
    }

    @Override
    public Optional<Entity> exportEntity(EntityDescriptor entityDescriptor, EntityDescriptorIds entityDescriptorIds) {
        final ModelId modelId = entityDescriptor.id();
        final Optional<EventDefinitionDto> eventDefinition = eventDefinitionService.get(modelId.id());
        if (!eventDefinition.isPresent()) {
            LOG.debug("Couldn't find event definition {}", entityDescriptor);
            return Optional.empty();
        }
        return Optional.of(exportNativeEntity(eventDefinition.get(), entityDescriptorIds));
    }

    private ImmutableSet<Constraint> versionConstraints(EventDefinitionDto eventDefinitionDto) {
        final String packageName = eventDefinitionDto.config().getContentPackPluginPackage();
        return pluginMetaData.stream()
                .filter(metaData -> packageName.equals(metaData.getClass().getCanonicalName()))
                .map(PluginVersionConstraint::of)
                .collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public NativeEntity<EventDefinitionDto> createNativeEntity(Entity entity,
                                                               Map<String, ValueReference> parameters,
                                                               Map<EntityDescriptor, Object> nativeEntities,
                                                               String username) {
        if (entity instanceof EntityV1) {
            final User user = Optional.ofNullable(userService.load(username)).orElseThrow(() -> new IllegalStateException("Cannot load user <" + username + "> from db"));
            return decode((EntityV1) entity, parameters, nativeEntities, user);
        } else {
            throw new IllegalArgumentException("Unsupported entity version: " + entity.getClass());
        }
    }

    private NativeEntity<EventDefinitionDto> decode(EntityV1 entity,
                                                    Map<String, ValueReference> parameters,
                                                    Map<EntityDescriptor, Object> nativeEntities, User user) {
        final EventDefinitionEntity eventDefinitionEntity = objectMapper.convertValue(entity.data(),
                EventDefinitionEntity.class);
        final EventDefinitionDto eventDefinition = eventDefinitionEntity.toNativeEntity(parameters, nativeEntities);
        final EventDefinitionDto savedDto;
        if (eventDefinitionEntity.isScheduled().asBoolean(parameters)) {
            savedDto = eventDefinitionHandler.create(eventDefinition, Optional.ofNullable(user));
        } else {
            savedDto = eventDefinitionHandler.createWithoutSchedule(eventDefinition, Optional.ofNullable(user));
        }
        return NativeEntity.create(entity.id(), savedDto.id(), ModelTypes.EVENT_DEFINITION_V1, savedDto.title(), savedDto);
    }

    @Override
    public Optional<NativeEntity<EventDefinitionDto>> loadNativeEntity(NativeEntityDescriptor nativeEntityDescriptor) {
        final Optional<EventDefinitionDto> eventDefinition = eventDefinitionService.get(nativeEntityDescriptor.id().id());

        return eventDefinition.map(eventDefinitionDto ->
                NativeEntity.create(nativeEntityDescriptor, eventDefinitionDto));
    }

    @Override
    public void delete(EventDefinitionDto nativeEntity) {
        eventDefinitionHandler.delete(nativeEntity.id());
    }

    @Override
    public EntityExcerpt createExcerpt(EventDefinitionDto nativeEntity) {
        return EntityExcerpt.builder()
                .id(ModelId.of(nativeEntity.id()))
                .type(ModelTypes.EVENT_DEFINITION_V1)
                .title(nativeEntity.title())
                .build();
    }

    @Override
    public Set<EntityExcerpt> listEntityExcerpts() {
        return eventDefinitionService.streamAll()
                .map(this::createExcerpt)
                .collect(Collectors.toSet());
    }

    @Override
    public Graph<EntityDescriptor> resolveNativeEntity(EntityDescriptor entityDescriptor) {
        final MutableGraph<EntityDescriptor> mutableGraph = GraphBuilder.directed().build();
        mutableGraph.addNode(entityDescriptor);

        final ModelId modelId = entityDescriptor.id();
        final Optional<EventDefinitionDto> eventDefinition = eventDefinitionService.get(modelId.id());
        if (eventDefinition.isPresent()) {
            eventDefinition.get().resolveNativeEntity(entityDescriptor, mutableGraph);
        } else {
            LOG.debug("Couldn't find event definition {}", entityDescriptor);
        }

        return ImmutableGraph.copyOf(mutableGraph);
    }

    @Override
    public Graph<Entity> resolveForInstallation(Entity entity, Map<String, ValueReference> parameters, Map<EntityDescriptor, Entity> entities) {
        if (entity instanceof EntityV1) {
            return resolveForInstallationV1((EntityV1) entity, parameters, entities);
        } else {
            throw new IllegalArgumentException("Unsupported entity version: " + entity.getClass());
        }
    }

    private Graph<Entity> resolveForInstallationV1(EntityV1 entity, Map<String, ValueReference> parameters, Map<EntityDescriptor, Entity> entities) {
        final MutableGraph<Entity> graph = GraphBuilder.directed().build();
        graph.addNode(entity);

        final EventDefinitionEntity eventDefinition = objectMapper.convertValue(entity.data(), EventDefinitionEntity.class);
        eventDefinition.resolveForInstallation(entity, parameters, entities, graph);

        return ImmutableGraph.copyOf(graph);
    }
}
