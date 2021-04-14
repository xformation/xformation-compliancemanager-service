/*
 * */
package com.synectiks.process.server.contentpacks.facades;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;
import com.google.common.primitives.Ints;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.contentpacks.exceptions.ContentPackException;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelType;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.constraints.Constraint;
import com.synectiks.process.server.contentpacks.model.constraints.PluginVersionConstraint;
import com.synectiks.process.server.contentpacks.model.entities.ConverterEntity;
import com.synectiks.process.server.contentpacks.model.entities.Entity;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.EntityExcerpt;
import com.synectiks.process.server.contentpacks.model.entities.EntityV1;
import com.synectiks.process.server.contentpacks.model.entities.ExtractorEntity;
import com.synectiks.process.server.contentpacks.model.entities.GrokPatternEntity;
import com.synectiks.process.server.contentpacks.model.entities.InputEntity;
import com.synectiks.process.server.contentpacks.model.entities.LookupTableEntity;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntity;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.references.ReferenceMap;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.grok.GrokPatternService;
import com.synectiks.process.server.inputs.Input;
import com.synectiks.process.server.inputs.InputService;
import com.synectiks.process.server.inputs.converters.ConverterFactory;
import com.synectiks.process.server.inputs.extractors.ExtractorFactory;
import com.synectiks.process.server.inputs.extractors.GrokExtractor;
import com.synectiks.process.server.inputs.extractors.LookupTableExtractor;
import com.synectiks.process.server.lookup.db.DBLookupTableService;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.configuration.ConfigurationException;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.inputs.Converter;
import com.synectiks.process.server.plugin.inputs.Extractor;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.shared.inputs.InputRegistry;
import com.synectiks.process.server.shared.inputs.MessageInputFactory;
import com.synectiks.process.server.shared.inputs.NoSuchInputTypeException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.synectiks.process.server.contentpacks.model.entities.references.ReferenceMapUtils.toReferenceMap;
import static com.synectiks.process.server.contentpacks.model.entities.references.ReferenceMapUtils.toValueMap;

public class InputFacade implements EntityFacade<InputWithExtractors> {
    private static final Logger LOG = LoggerFactory.getLogger(InputFacade.class);

    public static final ModelType TYPE_V1 = ModelTypes.INPUT_V1;

    private final ObjectMapper objectMapper;
    private final InputService inputService;
    private final DBLookupTableService lookupTableService;
    private final GrokPatternService grokPatternService;
    private final InputRegistry inputRegistry;
    private final MessageInputFactory messageInputFactory;
    private final ExtractorFactory extractorFactory;
    private final ConverterFactory converterFactory;
    private final ServerStatus serverStatus;
    private final Set<PluginMetaData> pluginMetaData;
    private final Map<String, MessageInput.Factory<? extends MessageInput>> inputFactories;

    @Inject
    public InputFacade(ObjectMapper objectMapper,
                       InputService inputService,
                       InputRegistry inputRegistry,
                       DBLookupTableService lookupTableService,
                       GrokPatternService grokPatternService,
                       MessageInputFactory messageInputFactory,
                       ExtractorFactory extractorFactory,
                       ConverterFactory converterFactory,
                       ServerStatus serverStatus,
                       Set<PluginMetaData> pluginMetaData,
                       Map<String, MessageInput.Factory<? extends MessageInput>> inputFactories) {
        this.objectMapper = objectMapper;
        this.inputService = inputService;
        this.lookupTableService = lookupTableService;
        this.grokPatternService = grokPatternService;
        this.inputRegistry = inputRegistry;
        this.messageInputFactory = messageInputFactory;
        this.extractorFactory = extractorFactory;
        this.converterFactory = converterFactory;
        this.serverStatus = serverStatus;
        this.pluginMetaData = pluginMetaData;
        this.inputFactories = inputFactories;
    }

    @VisibleForTesting
    Entity exportNativeEntity(InputWithExtractors inputWithExtractors, EntityDescriptorIds entityDescriptorIds) {
        final Input input = inputWithExtractors.input();

        // TODO: Create independent representation of entity?
        final Map<String, ValueReference> staticFields = input.getStaticFields().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, kv -> ValueReference.of(kv.getValue())));
        final ReferenceMap configuration = toReferenceMap(input.getConfiguration());
        final List<ExtractorEntity> extractors = inputWithExtractors.extractors().stream()
                .map(this::encodeExtractor)
                .collect(Collectors.toList());
        final InputEntity inputEntity = InputEntity.create(
                ValueReference.of(input.getTitle()),
                configuration,
                staticFields,
                ValueReference.of(input.getType()),
                ValueReference.of(input.isGlobal()),
                extractors);
        final JsonNode data = objectMapper.convertValue(inputEntity, JsonNode.class);
        final Set<Constraint> constraints = versionConstraints(input);
        return EntityV1.builder()
                .id(ModelId.of(entityDescriptorIds.getOrThrow(input.getId(), ModelTypes.INPUT_V1)))
                .type(ModelTypes.INPUT_V1)
                .data(data)
                .constraints(ImmutableSet.copyOf(constraints))
                .build();
    }

    private Set<Constraint> versionConstraints(Input input) {
        // TODO: Find more robust method of identifying the providing plugin
        final MessageInput.Factory<? extends MessageInput> inputFactory = inputFactories.get(input.getType());
        if (inputFactory == null) {
            throw new ContentPackException("Unknown input type: " + input.getType());
        }
        // We have to use the descriptor because the factory is only a runtime-generated proxy. :(
        final String packageName = inputFactory.getDescriptor().getClass().getPackage().getName();
        return pluginMetaData.stream()
                .filter(metaData -> packageName.startsWith(metaData.getClass().getPackage().getName()))
                .map(PluginVersionConstraint::of)
                .collect(Collectors.toSet());
    }

    private ExtractorEntity encodeExtractor(Extractor extractor) {
        final List<ConverterEntity> converters = extractor.getConverters().stream()
                .map(this::encodeConverter)
                .collect(Collectors.toList());
        return ExtractorEntity.create(
                ValueReference.of(extractor.getTitle()),
                ValueReference.of(extractor.getType()),
                ValueReference.of(extractor.getCursorStrategy()),
                ValueReference.of(extractor.getTargetField()),
                ValueReference.of(extractor.getSourceField()),
                toReferenceMap(extractor.getExtractorConfig()),
                converters,
                ValueReference.of(extractor.getConditionType()),
                ValueReference.of(extractor.getConditionValue()),
                ValueReference.of(Ints.saturatedCast(extractor.getOrder())));
    }

    private ConverterEntity encodeConverter(Converter converter) {
        return ConverterEntity.create(
                ValueReference.of(converter.getType()),
                toReferenceMap(converter.getConfig()));
    }


    @Override
    public NativeEntity<InputWithExtractors> createNativeEntity(Entity entity,
                                                                Map<String, ValueReference> parameters,
                                                                Map<EntityDescriptor, Object> nativeEntities,
                                                                String username) {
        if (entity instanceof EntityV1) {
            return decode((EntityV1) entity, parameters, username);
        } else {
            throw new IllegalArgumentException("Unsupported entity version: " + entity.getClass());
        }
    }

    private NativeEntity<InputWithExtractors> decode(EntityV1 entity, Map<String, ValueReference> parameters, String username) {
        final InputEntity inputEntity = objectMapper.convertValue(entity.data(), InputEntity.class);
        final Map<String, ValueReference> staticFields = inputEntity.staticFields();

        final MessageInput messageInput;
        try {
            messageInput = createMessageInput(
                    inputEntity.title().asString(parameters),
                    inputEntity.type().asString(parameters),
                    inputEntity.global().asBoolean(parameters),
                    toValueMap(inputEntity.configuration(), parameters),
                    username);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't create input", e);
        }

        final Input input;
        try {
            input = inputService.find(messageInput.getPersistId());
        } catch (NotFoundException e) {
            throw new RuntimeException("Couldn't find persisted input", e);
        }

        try {
            addStaticFields(input, messageInput, staticFields, parameters);
        } catch (ValidationException e) {
            throw new RuntimeException("Couldn't add static fields to input", e);
        }
        final List<Extractor> extractors;
        try {
            extractors = createExtractors(input, inputEntity.extractors(), username, parameters);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't create extractors", e);
        }

        return NativeEntity.create(entity.id(), input.getId(), TYPE_V1, input.getTitle(), InputWithExtractors.create(input, extractors));
    }

    private MessageInput createMessageInput(
            final String title,
            final String type,
            final boolean global,
            final Map<String, Object> configuration,
            final String username)
            throws NoSuchInputTypeException, ConfigurationException, ValidationException {
        final Configuration inputConfig = new Configuration(configuration);
        final DateTime createdAt = Tools.nowUTC();

        final MessageInput messageInput = messageInputFactory.create(type, inputConfig);
        messageInput.setTitle(title);
        messageInput.setGlobal(global);
        messageInput.setCreatorUserId(username);
        messageInput.setCreatedAt(createdAt);

        messageInput.checkConfiguration();

        // Don't run if exclusive and another instance is already running.
        if (messageInput.isExclusive() && inputRegistry.hasTypeRunning(messageInput.getClass())) {
            LOG.error("Input type <{}> of input <{}> is exclusive and already has input running.",
                    messageInput.getClass(), messageInput.getTitle());
        }

        final Input mongoInput = inputService.create(buildMongoDbInput(title, type, global, configuration, username, createdAt));

        // Persist input
        final String persistId = inputService.save(mongoInput);
        messageInput.setPersistId(persistId);
        messageInput.initialize();

        return messageInput;
    }


    private List<Extractor> createExtractors(final Input input,
                                             final List<ExtractorEntity> extractorEntities,
                                             final String username,
                                             final Map<String, ValueReference> parameters)
            throws Extractor.ReservedFieldException, com.synectiks.process.server.ConfigurationException,
            ExtractorFactory.NoSuchExtractorException, ValidationException {
        final ImmutableList.Builder<Extractor> result = ImmutableList.builder();
        for (ExtractorEntity extractorEntity : extractorEntities) {
            final List<Converter> converters = createConverters(extractorEntity.converters(), parameters);
            final Extractor extractor = addExtractor(
                    input,
                    extractorEntity.title().asString(parameters),
                    extractorEntity.order().asInteger(parameters),
                    extractorEntity.cursorStrategy().asEnum(parameters, Extractor.CursorStrategy.class),
                    extractorEntity.type().asEnum(parameters, Extractor.Type.class),
                    extractorEntity.sourceField().asString(parameters),
                    extractorEntity.targetField().asString(parameters),
                    toValueMap(extractorEntity.configuration(), parameters),
                    converters,
                    extractorEntity.conditionType().asEnum(parameters, Extractor.ConditionType.class),
                    extractorEntity.conditionValue().asString(parameters),
                    username);
            result.add(extractor);
        }

        return result.build();
    }

    private Extractor addExtractor(
            final Input input,
            final String title,
            final int order,
            final Extractor.CursorStrategy cursorStrategy,
            final Extractor.Type type,
            final String sourceField,
            final String targetField,
            final Map<String, Object> configuration,
            final List<Converter> converters,
            final Extractor.ConditionType conditionType,
            final String conditionValue,
            final String username)
            throws ValidationException, com.synectiks.process.server.ConfigurationException,
            ExtractorFactory.NoSuchExtractorException, Extractor.ReservedFieldException {
        final String extractorId = UUID.randomUUID().toString();
        final Extractor extractor = extractorFactory.factory(
                extractorId,
                title,
                order,
                cursorStrategy,
                type,
                sourceField,
                targetField,
                configuration,
                username,
                converters,
                conditionType,
                conditionValue);

        inputService.addExtractor(input, extractor);

        return extractor;
    }


    private List<Converter> createConverters(final List<ConverterEntity> requestedConverters,
                                             final Map<String, ValueReference> parameters) {
        final ImmutableList.Builder<Converter> converters = ImmutableList.builder();

        for (final ConverterEntity converterEntity : requestedConverters) {
            try {
                final Converter converter = converterFactory.create(
                        converterEntity.type().asEnum(parameters, Converter.Type.class),
                        toValueMap(converterEntity.configuration(), parameters));
                converters.add(converter);
            } catch (ConverterFactory.NoSuchConverterException e) {
                LOG.warn("No such converter [" + converterEntity.type() + "]. Skipping.", e);
            } catch (com.synectiks.process.server.ConfigurationException e) {
                LOG.warn("Missing configuration for [" + converterEntity.type() + "]. Skipping.", e);
            }
        }

        return converters.build();
    }

    private void addStaticFields(final Input input, final MessageInput messageInput,
                                 final Map<String, ValueReference> staticFields,
                                 final Map<String, ValueReference> parameters)
            throws ValidationException {
        for (Map.Entry<String, ValueReference> staticField : staticFields.entrySet()) {
            addStaticField(input, messageInput, staticField.getKey(), staticField.getValue().asString(parameters));
        }
    }

    private void addStaticField(final Input input,
                                final MessageInput messageInput,
                                final String key,
                                final String value)
            throws ValidationException {
        // Check if key is a valid message key.
        if (!Message.validKey(key)) {
            final String errorMessage = "Invalid key: [" + key + "]";
            LOG.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (isNullOrEmpty(key) || isNullOrEmpty(value)) {
            final String errorMessage = "Missing attributes: key=[" + key + "], value=[" + value + "]";
            LOG.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (Message.RESERVED_FIELDS.contains(key) && !Message.RESERVED_SETTABLE_FIELDS.contains(key)) {
            final String errorMessage = "Cannot add static field. Field [" + key + "] is reserved.";
            LOG.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        // Seriously, why?
        messageInput.addStaticField(key, value);
        inputService.addStaticField(input, key, value);
    }

    private Map<String, Object> buildMongoDbInput(
            final String title,
            final String type,
            final boolean global,
            final Map<String, Object> configuration,
            final String userName,
            final DateTime createdAt) {
        final ImmutableMap.Builder<String, Object> inputData = ImmutableMap.builder();
        inputData.put(MessageInput.FIELD_TITLE, title);
        inputData.put(MessageInput.FIELD_TYPE, type);
        inputData.put(MessageInput.FIELD_CREATOR_USER_ID, userName);
        inputData.put(MessageInput.FIELD_CONFIGURATION, configuration);
        inputData.put(MessageInput.FIELD_CREATED_AT, createdAt);

        if (global) {
            inputData.put(MessageInput.FIELD_GLOBAL, true);
        } else {
            inputData.put(MessageInput.FIELD_NODE_ID, serverStatus.getNodeId().toString());
        }

        return inputData.build();
    }

    @Override
    public Optional<NativeEntity<InputWithExtractors>> loadNativeEntity(NativeEntityDescriptor nativeEntityDescriptor) {
        try {
            final InputWithExtractors input = InputWithExtractors.create(inputService.find(nativeEntityDescriptor.id().id()));
            return Optional.of(NativeEntity.create(nativeEntityDescriptor, input));
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(InputWithExtractors nativeEntity) {
        inputService.destroy(nativeEntity.input());
    }

    @Override
    public EntityExcerpt createExcerpt(InputWithExtractors inputWithExtractors) {
        return EntityExcerpt.builder()
                .id(ModelId.of(inputWithExtractors.input().getId()))
                .type(ModelTypes.INPUT_V1)
                .title(inputWithExtractors.input().getTitle())
                .build();
    }

    @Override
    public Set<EntityExcerpt> listEntityExcerpts() {
        return inputService.all().stream()
                .map(InputWithExtractors::create)
                .map(this::createExcerpt)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<Entity> exportEntity(EntityDescriptor entityDescriptor, EntityDescriptorIds entityDescriptorIds) {
        final ModelId modelId = entityDescriptor.id();

        try {
            final Input input = inputService.find(modelId.id());
            final InputWithExtractors inputWithExtractors = InputWithExtractors.create(input, inputService.getExtractors(input));
            return Optional.of(exportNativeEntity(inputWithExtractors, entityDescriptorIds));
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public Graph<EntityDescriptor> resolveNativeEntity(EntityDescriptor entityDescriptor) {
        final MutableGraph<EntityDescriptor> mutableGraph = GraphBuilder.directed().build();
        mutableGraph.addNode(entityDescriptor);

        final ModelId modelId = entityDescriptor.id();
        try {
            final Input input = inputService.find(modelId.toString());
            final InputWithExtractors inputWithExtractors = InputWithExtractors.create(input, inputService.getExtractors(input));

            resolveNativeEntityLookupTable(entityDescriptor, inputWithExtractors, mutableGraph);
            resolveNativeEntityGrokPattern(entityDescriptor, inputWithExtractors, mutableGraph);

            return ImmutableGraph.copyOf(mutableGraph);
        } catch (NotFoundException e) {
            LOG.debug("Couldn't find input {}", entityDescriptor, e);
        }
        return ImmutableGraph.copyOf(mutableGraph);
    }

    private void resolveNativeEntityGrokPattern(EntityDescriptor entityDescriptor,
                                                InputWithExtractors inputWithExtractors,
                                                MutableGraph<EntityDescriptor> mutableGraph) {
        inputWithExtractors.extractors().stream()
                .filter(e -> e.getType().equals(Extractor.Type.GROK))
                .map(e -> (String) e.getExtractorConfig().get(GrokExtractor.CONFIG_GROK_PATTERN))
                .map(GrokPatternService::extractPatternNames)
                .flatMap(Collection::stream)
                .forEach(patternName -> {
                    grokPatternService.loadByName(patternName).ifPresent(depPattern -> {
                        final EntityDescriptor depEntityDescriptor = EntityDescriptor.create(
                                depPattern.id(), ModelTypes.GROK_PATTERN_V1);
                        mutableGraph.putEdge(entityDescriptor, depEntityDescriptor);
                    });
                });
    }

    private void resolveNativeEntityLookupTable(EntityDescriptor entityDescriptor,
                                                InputWithExtractors inputWithExtractors,
                                                MutableGraph<EntityDescriptor> mutableGraph) {

        final Stream<String> extractorLookupNames = inputWithExtractors.extractors().stream()
                .filter(e -> e.getType().equals(Extractor.Type.LOOKUP_TABLE))
                .map(e -> (String) e.getExtractorConfig().get(LookupTableExtractor.CONFIG_LUT_NAME));
        final Stream<String> converterLookupNames = inputWithExtractors.extractors().stream()
                .flatMap(e -> e.getConverters().stream())
                .filter(c -> c.getType().equals(Converter.Type.LOOKUP_TABLE))
                .map(c -> (String) c.getConfig().get("lookup_table_name"));

        Stream.concat(extractorLookupNames, converterLookupNames)
                .map(lookupTableService::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(lookupTableDto -> {
                    EntityDescriptor lookupTable = EntityDescriptor.create(
                            ModelId.of(lookupTableDto.id()), ModelTypes.LOOKUP_TABLE_V1);
                    mutableGraph.putEdge(entityDescriptor, lookupTable);
                });
    }

    @Override
    public Graph<Entity> resolveForInstallation(Entity entity,
                                                Map<String, ValueReference> parameters,
                                                Map<EntityDescriptor, Entity> entities) {
        if(entity instanceof EntityV1) {
            return resolveForInstallationV1((EntityV1) entity, parameters, entities);
        } else {
            throw new IllegalArgumentException("Unsupported entity version: " + entity.getClass());
        }
    }

    private Graph<Entity> resolveForInstallationV1(EntityV1 entity,
                                                   Map<String, ValueReference> parameters,
                                                   Map<EntityDescriptor, Entity> entities) {
        final MutableGraph<Entity> graph = GraphBuilder.directed().build();
        graph.addNode(entity);

        final InputEntity input = objectMapper.convertValue(entity.data(), InputEntity.class);

        resolveForInstallationV1LookupTable(entity, input, parameters, entities, graph);
        resolveForInstallationV1GrokPattern(entity, input, parameters, entities, graph);

        return ImmutableGraph.copyOf(graph);
    }

    private void resolveForInstallationV1GrokPattern(EntityV1 entity,
                                                InputEntity input,
                                                Map<String, ValueReference> parameters,
                                                Map<EntityDescriptor, Entity> entities,
                                                MutableGraph<Entity> graph) {
        input.extractors().stream()
                .filter(e -> e.type().asString(parameters).equals(Extractor.Type.GROK.toString()))
                .map(ExtractorEntity::configuration)
                .map(c -> ((ValueReference) c.get(GrokExtractor.CONFIG_GROK_PATTERN)).asString(parameters))
                .map(GrokPatternService::extractPatternNames)
                .flatMap(Collection::stream)
                .forEach(patternName -> {
                    entities.entrySet().stream()
                            .filter(x -> x.getValue().type().equals(ModelTypes.GROK_PATTERN_V1))
                            .filter(x -> {
                                EntityV1 entityV1 = (EntityV1) x.getValue();
                                GrokPatternEntity grokPatternEntity1 = objectMapper.convertValue(entityV1.data(),
                                        GrokPatternEntity.class);
                                return grokPatternEntity1.name().equals(patternName);
                            }).forEach(x -> graph.putEdge(entity, x.getValue()));
                });
    }

    private void resolveForInstallationV1LookupTable(EntityV1 entity,
                                                     InputEntity input,
                                                     Map<String, ValueReference> parameters,
                                                     Map<EntityDescriptor, Entity> entities,
                                                     MutableGraph<Entity> graph) {
        final Set<String> lookupTableNames = input.extractors().stream()
                .filter(e -> e.type().asString(parameters).equals(Extractor.Type.LOOKUP_TABLE.toString()))
                .map(ExtractorEntity::configuration)
                .map(c -> ((ValueReference) c.get(LookupTableExtractor.CONFIG_LUT_NAME)).asString(parameters))
                .collect(Collectors.toSet());

        input.extractors().stream().flatMap(c -> c.converters().stream())
                .filter(con -> con.type().asString(parameters).equals(Converter.Type.LOOKUP_TABLE.name()))
                .map(con -> ((ValueReference) con.configuration().get("lookup_table_name")).asString(parameters))
                .forEach(lookupTableNames::add);

        entities.entrySet().stream()
                .filter(x -> x.getValue().type().equals(ModelTypes.LOOKUP_TABLE_V1))
                .filter(x -> {
                    EntityV1 entityV1 = (EntityV1) x.getValue();
                    LookupTableEntity lookupTableEntity = objectMapper.convertValue(entityV1.data(), LookupTableEntity.class);
                    return  lookupTableNames.contains(lookupTableEntity.name().asString(parameters));
                })
                .forEach(x -> graph.putEdge(entity, x.getValue()));
    }
}
