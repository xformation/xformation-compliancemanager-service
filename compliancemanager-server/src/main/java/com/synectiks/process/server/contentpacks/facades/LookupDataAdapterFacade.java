/*
 * */
package com.synectiks.process.server.contentpacks.facades;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelType;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.constraints.Constraint;
import com.synectiks.process.server.contentpacks.model.constraints.PluginVersionConstraint;
import com.synectiks.process.server.contentpacks.model.entities.Entity;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.EntityExcerpt;
import com.synectiks.process.server.contentpacks.model.entities.EntityV1;
import com.synectiks.process.server.contentpacks.model.entities.LookupDataAdapterEntity;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntity;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.jackson.TypeReferences;
import com.synectiks.process.server.lookup.db.DBDataAdapterService;
import com.synectiks.process.server.lookup.dto.DataAdapterDto;
import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.plugin.lookup.LookupDataAdapterConfiguration;

import javax.inject.Inject;

import static com.synectiks.process.server.contentpacks.model.entities.references.ReferenceMapUtils.toReferenceMap;
import static com.synectiks.process.server.contentpacks.model.entities.references.ReferenceMapUtils.toValueMap;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class LookupDataAdapterFacade implements EntityFacade<DataAdapterDto> {
    public static final ModelType TYPE_V1 = ModelTypes.LOOKUP_ADAPTER_V1;

    private final ObjectMapper objectMapper;
    private final DBDataAdapterService dataAdapterService;
    private final Set<PluginMetaData> pluginMetaData;

    @Inject
    public LookupDataAdapterFacade(ObjectMapper objectMapper,
                                   DBDataAdapterService dataAdapterService,
                                   Set<PluginMetaData> pluginMetaData) {
        this.objectMapper = objectMapper;
        this.dataAdapterService = dataAdapterService;
        this.pluginMetaData = pluginMetaData;
    }

    @VisibleForTesting
    Entity exportNativeEntity(DataAdapterDto dataAdapterDto, EntityDescriptorIds entityDescriptorIds) {
        // TODO: Create independent representation of entity?
        final Map<String, Object> configuration = objectMapper.convertValue(dataAdapterDto.config(), TypeReferences.MAP_STRING_OBJECT);
        final LookupDataAdapterEntity lookupDataAdapterEntity = LookupDataAdapterEntity.create(
                ValueReference.of(dataAdapterDto.name()),
                ValueReference.of(dataAdapterDto.title()),
                ValueReference.of(dataAdapterDto.description()),
                toReferenceMap(configuration));
        final JsonNode data = objectMapper.convertValue(lookupDataAdapterEntity, JsonNode.class);
        final Set<Constraint> constraints = versionConstraints(dataAdapterDto);
        return EntityV1.builder()
                .id(ModelId.of(entityDescriptorIds.getOrThrow(dataAdapterDto.id(), ModelTypes.LOOKUP_ADAPTER_V1)))
                .type(ModelTypes.LOOKUP_ADAPTER_V1)
                .constraints(ImmutableSet.copyOf(constraints))
                .data(data)
                .build();
    }


    private Set<Constraint> versionConstraints(DataAdapterDto dataAdapterDto) {
        // TODO: Find more robust method of identifying the providing plugin
        final String packageName = dataAdapterDto.config().getClass().getPackage().getName();
        return pluginMetaData.stream()
                .filter(metaData -> packageName.startsWith(metaData.getClass().getPackage().getName()))
                .map(PluginVersionConstraint::of)
                .collect(Collectors.toSet());
    }

    @Override
    public NativeEntity<DataAdapterDto> createNativeEntity(Entity entity,
                                                           Map<String, ValueReference> parameters,
                                                           Map<EntityDescriptor, Object> nativeEntities,
                                                           String username) {
        if (entity instanceof EntityV1) {
            return decode((EntityV1) entity, parameters);
        } else {
            throw new IllegalArgumentException("Unsupported entity version: " + entity.getClass());
        }
    }

    private NativeEntity<DataAdapterDto> decode(EntityV1 entity, final Map<String, ValueReference> parameters) {
        final LookupDataAdapterEntity lookupDataAdapterEntity = objectMapper.convertValue(entity.data(), LookupDataAdapterEntity.class);
        final LookupDataAdapterConfiguration configuration = objectMapper.convertValue(toValueMap(lookupDataAdapterEntity.configuration(), parameters), LookupDataAdapterConfiguration.class);
        final DataAdapterDto dataAdapterDto = DataAdapterDto.builder()
                .name(lookupDataAdapterEntity.name().asString(parameters))
                .title(lookupDataAdapterEntity.title().asString(parameters))
                .description(lookupDataAdapterEntity.description().asString(parameters))
                .config(configuration)
                .build();

        final DataAdapterDto savedDataAdapterDto = dataAdapterService.save(dataAdapterDto);
        return NativeEntity.create(entity.id(), savedDataAdapterDto.id(), TYPE_V1, savedDataAdapterDto.title(), savedDataAdapterDto);
    }

    @Override
    public Optional<NativeEntity<DataAdapterDto>> findExisting(Entity entity, Map<String, ValueReference> parameters) {
        if (entity instanceof EntityV1) {
            return findExisting((EntityV1) entity, parameters);
        } else {
            throw new IllegalArgumentException("Unsupported entity version: " + entity.getClass());
        }
    }

    private Optional<NativeEntity<DataAdapterDto>> findExisting(EntityV1 entity, Map<String, ValueReference> parameters) {
        final LookupDataAdapterEntity dataAdapterEntity = objectMapper.convertValue(entity.data(), LookupDataAdapterEntity.class);
        final String name = dataAdapterEntity.name().asString(parameters);

        final Optional<DataAdapterDto> existingDataAdapter = dataAdapterService.get(name);

        return existingDataAdapter.map(dataAdapter -> NativeEntity.create(entity.id(), dataAdapter.id(), TYPE_V1, dataAdapter.title(), dataAdapter));
    }

    @Override
    public Optional<NativeEntity<DataAdapterDto>> loadNativeEntity(NativeEntityDescriptor nativeEntityDescriptor) {
        return dataAdapterService.get(nativeEntityDescriptor.id().id())
                .map(entity -> NativeEntity.create(nativeEntityDescriptor, entity));
    }

    @Override
    public void delete(DataAdapterDto nativeEntity) {
        dataAdapterService.delete(nativeEntity.id());
    }

    @Override
    public EntityExcerpt createExcerpt(DataAdapterDto dataAdapterDto) {
        return EntityExcerpt.builder()
                .id(ModelId.of(dataAdapterDto.id()))
                .type(ModelTypes.LOOKUP_ADAPTER_V1)
                .title(dataAdapterDto.title())
                .build();
    }

    @Override
    public Set<EntityExcerpt> listEntityExcerpts() {
        return dataAdapterService.findAll().stream()
                .map(this::createExcerpt)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<Entity> exportEntity(EntityDescriptor entityDescriptor, EntityDescriptorIds entityDescriptorIds) {
        final ModelId modelId = entityDescriptor.id();
        return dataAdapterService.get(modelId.id()).map(dataAdapterDto -> exportNativeEntity(dataAdapterDto, entityDescriptorIds));
    }
}
