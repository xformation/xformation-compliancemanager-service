/*
 * */
package com.synectiks.process.server.contentpacks.facades;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;
import com.synectiks.process.common.events.legacy.V20190722150700_LegacyAlertConditionMigration;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackConfiguration;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackConfigurationService;
import com.synectiks.process.server.alerts.AlertService;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.contentpacks.exceptions.ContentPackException;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelType;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.entities.Entity;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.EntityExcerpt;
import com.synectiks.process.server.contentpacks.model.entities.EntityV1;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntity;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.StreamAlarmCallbackEntity;
import com.synectiks.process.server.contentpacks.model.entities.StreamAlertConditionEntity;
import com.synectiks.process.server.contentpacks.model.entities.StreamEntity;
import com.synectiks.process.server.contentpacks.model.entities.StreamRuleEntity;
import com.synectiks.process.server.contentpacks.model.entities.references.ReferenceMapUtils;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.indexer.indexset.IndexSetService;
import com.synectiks.process.server.plugin.alarms.AlertCondition;
import com.synectiks.process.server.plugin.configuration.ConfigurationException;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.plugin.streams.Output;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.plugin.streams.StreamRule;
import com.synectiks.process.server.plugin.streams.StreamRuleType;
import com.synectiks.process.server.rest.models.alarmcallbacks.requests.CreateAlarmCallbackRequest;
import com.synectiks.process.server.rest.models.streams.alerts.requests.CreateConditionRequest;
import com.synectiks.process.server.rest.resources.streams.requests.CreateStreamRequest;
import com.synectiks.process.server.rest.resources.streams.rules.requests.CreateStreamRuleRequest;
import com.synectiks.process.server.shared.users.UserService;
import com.synectiks.process.server.streams.StreamRuleService;
import com.synectiks.process.server.streams.StreamService;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.nullToEmpty;

public class StreamFacade implements EntityFacade<Stream> {
    private static final Logger LOG = LoggerFactory.getLogger(StreamFacade.class);
    private static final String DUMMY_STREAM_ID = "ffffffffffffffffffffffff";

    public static final ModelType TYPE_V1 = ModelTypes.STREAM_V1;

    private final ObjectMapper objectMapper;
    private final StreamService streamService;
    private final StreamRuleService streamRuleService;
    private final AlertService streamAlertService;
    private final AlarmCallbackConfigurationService alarmCallbackConfigurationService;
    private final V20190722150700_LegacyAlertConditionMigration legacyAlertsMigration;
    private final IndexSetService indexSetService;
    private final UserService userService;

    @Inject
    public StreamFacade(ObjectMapper objectMapper,
                        StreamService streamService,
                        StreamRuleService streamRuleService,
                        AlertService streamAlertService,
                        AlarmCallbackConfigurationService alarmCallbackConfigurationService,
                        V20190722150700_LegacyAlertConditionMigration legacyAlertsMigration,
                        IndexSetService indexSetService, UserService userService) {
        this.objectMapper = objectMapper;
        this.streamService = streamService;
        this.streamRuleService = streamRuleService;
        this.streamAlertService = streamAlertService;
        this.alarmCallbackConfigurationService = alarmCallbackConfigurationService;
        this.legacyAlertsMigration = legacyAlertsMigration;
        this.indexSetService = indexSetService;
        this.userService = userService;
    }

    @VisibleForTesting
    Entity exportNativeEntity(Stream stream, EntityDescriptorIds entityDescriptorIds) {
        final List<StreamRuleEntity> streamRules = stream.getStreamRules().stream()
                .map(this::encodeStreamRule)
                .collect(Collectors.toList());
        final Set<ValueReference> outputIds = stream.getOutputs().stream()
                .map(output -> entityDescriptorIds.getOrThrow(output.getId(), ModelTypes.OUTPUT_V1))
                .map(ValueReference::of)
                .collect(Collectors.toSet());
        final StreamEntity streamEntity = StreamEntity.create(
                ValueReference.of(stream.getTitle()),
                ValueReference.of(stream.getDescription()),
                ValueReference.of(stream.getDisabled()),
                ValueReference.of(stream.getMatchingType()),
                streamRules,
                Collections.emptyList(), // Kept for backwards compatibility
                Collections.emptyList(), // Kept for backwards compatibility
                outputIds,
                ValueReference.of(stream.isDefaultStream()),
                ValueReference.of(stream.getRemoveMatchesFromDefaultStream()));

        final JsonNode data = objectMapper.convertValue(streamEntity, JsonNode.class);
        return EntityV1.builder()
                .id(ModelId.of(entityDescriptorIds.getOrThrow(stream.getId(), ModelTypes.STREAM_V1)))
                .type(ModelTypes.STREAM_V1)
                .data(data)
                .build();
    }

    private StreamRuleEntity encodeStreamRule(StreamRule streamRule) {
        return StreamRuleEntity.create(
                ValueReference.of(streamRule.getType()),
                ValueReference.of(streamRule.getField()),
                ValueReference.of(nullToEmpty(streamRule.getValue())), // Rule value can be null!
                ValueReference.of(streamRule.getInverted()),
                ValueReference.of(nullToEmpty(streamRule.getDescription()))); // Rule description can be null!
    }

    @Override
    public NativeEntity<Stream> createNativeEntity(Entity entity,
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

    private NativeEntity<Stream> decode(EntityV1 entity,
                                        Map<String, ValueReference> parameters,
                                        Map<EntityDescriptor, Object> nativeEntities,
                                        User user) {
        final StreamEntity streamEntity = objectMapper.convertValue(entity.data(), StreamEntity.class);
        final CreateStreamRequest createStreamRequest = CreateStreamRequest.create(
                streamEntity.title().asString(parameters),
                streamEntity.description().asString(parameters),
                null, // ignored
                null,
                streamEntity.matchingType().asString(parameters),
                streamEntity.removeMatches().asBoolean(parameters),
                indexSetService.getDefault().id());
        final Stream stream = streamService.create(createStreamRequest, user.getName());
        final List<StreamRule> streamRules = streamEntity.streamRules().stream()
                .map(streamRuleEntity -> createStreamRuleRequest(streamRuleEntity, parameters))
                .map(request -> streamRuleService.create(DUMMY_STREAM_ID, request))
                .collect(Collectors.toList());
        // TODO: The creation of legacy alert conditions should be avoided and a new event definition should be created instead
        final List<AlertCondition> alertConditions = streamEntity.alertConditions().stream()
                .map(alertCondition -> createStreamAlertConditionRequest(alertCondition, parameters))
                .map(request -> {
                    try {
                        return streamAlertService.fromRequest(request, stream, user.getName());
                    } catch (ConfigurationException e) {
                        throw new ContentPackException("Couldn't create entity " + entity.toEntityDescriptor(), e);
                    }
                })
                .collect(Collectors.toList());
        // TODO: The creation of legacy alarm callback should be avoided and a new event notification should be created instead
        final List<AlarmCallbackConfiguration> alarmCallbacks = streamEntity.alarmCallbacks().stream()
                .map(alarmCallback -> createStreamAlarmCallbackRequest(alarmCallback, parameters))
                .map(request -> alarmCallbackConfigurationService.create(stream.getId(), request, user.getName()))
                .collect(Collectors.toList());
        final String savedStreamId;
        try {
            savedStreamId = streamService.saveWithRulesAndOwnership(stream, streamRules, user);

            for (final AlertCondition alertCondition : alertConditions) {
                streamService.addAlertCondition(stream, alertCondition);
            }
            for (final AlarmCallbackConfiguration alarmCallback : alarmCallbacks) {
                alarmCallbackConfigurationService.save(alarmCallback);
            }
        } catch (ValidationException e) {
            throw new ContentPackException("Couldn't create entity " + entity.toEntityDescriptor(), e);
        }

        final Set<ObjectId> outputIds = streamEntity.outputs().stream()
                .map(valueReference -> valueReference.asString(parameters))
                .map(ModelId::of)
                .map(modelId -> EntityDescriptor.create(modelId, ModelTypes.OUTPUT_V1))
                .map(descriptor -> findOutput(descriptor, nativeEntities))
                .map(Output::getId)
                .map(ObjectId::new)
                .collect(Collectors.toSet());
        streamService.addOutputs(new ObjectId(savedStreamId), outputIds);

        if (!alertConditions.isEmpty() || !alarmCallbacks.isEmpty()) {
            // Migrated newly created legacy alert conditions and alarm callbacks to the new events system
            // TODO: Remove migration call once we updated the above code to directly create event definitions and notifications
            try {
                legacyAlertsMigration.upgrade();
            } catch (Exception e) {
                LOG.error("Couldn't run migration for newly created legacy alert conditions and/or alarm callbacks", e);
            }
        }

        return NativeEntity.create(entity.id(), savedStreamId, TYPE_V1, stream.getTitle(), stream);
    }

    private CreateConditionRequest createStreamAlertConditionRequest(StreamAlertConditionEntity alertCondition,
                                                                     Map<String, ValueReference> parameters) {
        return CreateConditionRequest.builder()
                .setType(alertCondition.type())
                .setTitle(alertCondition.title().asString(parameters))
                .setParameters(ReferenceMapUtils.toValueMap(alertCondition.parameters(), parameters))
                .build();
    }

    private CreateAlarmCallbackRequest createStreamAlarmCallbackRequest(StreamAlarmCallbackEntity alarmCallback,
                                                                        Map<String, ValueReference> parameters) {
        return CreateAlarmCallbackRequest.create(
                alarmCallback.type(),
                alarmCallback.title().asString(parameters),
                ReferenceMapUtils.toValueMap(alarmCallback.configuration(), parameters));
    }


    private CreateStreamRuleRequest createStreamRuleRequest(StreamRuleEntity streamRuleEntity, Map<String, ValueReference> parameters) {
        return CreateStreamRuleRequest.create(
                streamRuleEntity.type().asEnum(parameters, StreamRuleType.class).getValue(),
                streamRuleEntity.value().asString(parameters),
                streamRuleEntity.field().asString(parameters),
                streamRuleEntity.inverted().asBoolean(parameters),
                streamRuleEntity.description().asString(parameters));
    }

    private Output findOutput(EntityDescriptor outputDescriptor, Map<EntityDescriptor, Object> nativeEntities) {
        final Object output = nativeEntities.get(outputDescriptor);
        if (output == null) {
            throw new ContentPackException("Missing referenced output: " + outputDescriptor);
        } else if (output instanceof Output) {
            return (Output) output;
        } else {
            final String msg = "Invalid entity type for referenced output " + outputDescriptor + ": " + output.getClass();
            throw new ContentPackException(msg);
        }
    }

    @Override
    public Optional<NativeEntity<Stream>> findExisting(Entity entity, Map<String, ValueReference> parameters) {
        if (entity instanceof EntityV1) {
            return findExisting((EntityV1) entity, parameters);
        } else {
            throw new IllegalArgumentException("Unsupported entity version: " + entity.getClass());
        }
    }

    private Optional<NativeEntity<Stream>> findExisting(EntityV1 entity, Map<String, ValueReference> parameters) {
        final String streamId = entity.id().id();
        // Always use the existing default stream
        if (Stream.DEFAULT_STREAM_ID.equals(streamId)) {
            try {
                final Stream stream = streamService.load(Stream.DEFAULT_STREAM_ID);
                return Optional.of(NativeEntity.create(entity.id(), Stream.DEFAULT_STREAM_ID, ModelTypes.STREAM_V1, stream.getTitle(), stream));
            } catch (NotFoundException e) {
                throw new ContentPackException("Default stream <" + streamId + "> does not exist!", e);
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<NativeEntity<Stream>> loadNativeEntity(NativeEntityDescriptor nativeEntityDescriptor) {
        try {
            final Stream stream = streamService.load(nativeEntityDescriptor.id().id());
            return Optional.of(NativeEntity.create(nativeEntityDescriptor, stream));
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(Stream nativeEntity) {
        if (nativeEntity.isDefaultStream()) {
            LOG.debug("The default stream should not be deleted");
            return;
        }
        try {
            streamService.destroy(nativeEntity);
        } catch (NotFoundException ignore) {
        }
    }

    @Override
    public EntityExcerpt createExcerpt(Stream stream) {
        return EntityExcerpt.builder()
                .id(ModelId.of(stream.getId()))
                .type(ModelTypes.STREAM_V1)
                .title(stream.getTitle())
                .build();
    }

    @Override
    public Set<EntityExcerpt> listEntityExcerpts() {
        return streamService.loadAll().stream()
                .map(this::createExcerpt)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<Entity> exportEntity(EntityDescriptor entityDescriptor, EntityDescriptorIds entityDescriptorIds) {
        final ModelId modelId = entityDescriptor.id();
        try {
            final Stream stream = streamService.load(modelId.id());
            return Optional.of(exportNativeEntity(stream, entityDescriptorIds));
        } catch (NotFoundException e) {
            LOG.debug("Couldn't find stream {}", entityDescriptor, e);
            return Optional.empty();
        }
    }

    @Override
    public Graph<EntityDescriptor> resolveNativeEntity(EntityDescriptor entityDescriptor) {
        final MutableGraph<EntityDescriptor> mutableGraph = GraphBuilder.directed().build();
        mutableGraph.addNode(entityDescriptor);

        final ModelId modelId = entityDescriptor.id();
        try {
            final Stream stream = streamService.load(modelId.id());
            stream.getOutputs().stream()
                    .map(Output::getId)
                    .map(ModelId::of)
                    .map(id -> EntityDescriptor.create(id, ModelTypes.OUTPUT_V1))
                    .forEach(output -> mutableGraph.putEdge(entityDescriptor, output));
        } catch (NotFoundException e) {
            LOG.debug("Couldn't find stream {}", entityDescriptor, e);
        }

        return ImmutableGraph.copyOf(mutableGraph);
    }

    @Override
    public Graph<Entity> resolveForInstallation(Entity entity,
                                                Map<String, ValueReference> parameters,
                                                Map<EntityDescriptor, Entity> entities) {
        if (entity instanceof EntityV1) {
            return resolveForInstallation((EntityV1) entity, parameters, entities);
        } else {
            throw new IllegalArgumentException("Unsupported entity version: " + entity.getClass());
        }
    }

    private Graph<Entity> resolveForInstallation(EntityV1 entity,
                                                 Map<String, ValueReference> parameters,
                                                 Map<EntityDescriptor, Entity> entities) {
        final MutableGraph<Entity> mutableGraph = GraphBuilder.directed().build();
        mutableGraph.addNode(entity);

        final StreamEntity streamEntity = objectMapper.convertValue(entity.data(), StreamEntity.class);

        streamEntity.outputs().stream()
                .map(valueReference -> valueReference.asString(parameters))
                .map(ModelId::of)
                .map(modelId -> EntityDescriptor.create(modelId, ModelTypes.OUTPUT_V1))
                .map(entities::get)
                .filter(Objects::nonNull)
                .forEach(outputEntity -> mutableGraph.putEdge(entity, outputEntity));

        return ImmutableGraph.copyOf(mutableGraph);
    }
}
