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
import com.synectiks.process.server.contentpacks.model.entities.LookupCacheEntity;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntity;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.jackson.TypeReferences;
import com.synectiks.process.server.lookup.db.DBCacheService;
import com.synectiks.process.server.lookup.dto.CacheDto;
import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.plugin.lookup.LookupCacheConfiguration;

import javax.inject.Inject;

import static com.synectiks.process.server.contentpacks.model.entities.references.ReferenceMapUtils.toReferenceMap;
import static com.synectiks.process.server.contentpacks.model.entities.references.ReferenceMapUtils.toValueMap;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class LookupCacheFacade implements EntityFacade<CacheDto> {
    public static final ModelType TYPE_V1 = ModelTypes.LOOKUP_CACHE_V1;

    private final ObjectMapper objectMapper;
    private final DBCacheService cacheService;
    private final Set<PluginMetaData> pluginMetaData;

    @Inject
    public LookupCacheFacade(ObjectMapper objectMapper,
                             DBCacheService cacheService,
                             Set<PluginMetaData> pluginMetaData) {
        this.objectMapper = objectMapper;
        this.cacheService = cacheService;
        this.pluginMetaData = pluginMetaData;
    }

    @VisibleForTesting
    Entity exportNativeEntity(CacheDto cacheDto, EntityDescriptorIds entityDescriptorIds) {
        // TODO: Create independent representation of entity?
        final Map<String, Object> configuration = objectMapper.convertValue(cacheDto.config(), TypeReferences.MAP_STRING_OBJECT);
        final LookupCacheEntity lookupCacheEntity = LookupCacheEntity.create(
                ValueReference.of(cacheDto.name()),
                ValueReference.of(cacheDto.title()),
                ValueReference.of(cacheDto.description()),
                toReferenceMap(configuration));
        final JsonNode data = objectMapper.convertValue(lookupCacheEntity, JsonNode.class);
        final Set<Constraint> constraints = versionConstraints(cacheDto);
        return EntityV1.builder()
                .id(ModelId.of(entityDescriptorIds.getOrThrow(cacheDto.id(), ModelTypes.LOOKUP_CACHE_V1)))
                .type(ModelTypes.LOOKUP_CACHE_V1)
                .constraints(ImmutableSet.copyOf(constraints))
                .data(data)
                .build();
    }

    private Set<Constraint> versionConstraints(CacheDto cacheDto) {
        // TODO: Find more robust method of identifying the providing plugin
        final String packageName = cacheDto.config().getClass().getPackage().getName();

        return pluginMetaData.stream()
                .filter(metaData -> packageName.startsWith(metaData.getClass().getPackage().getName()))
                .map(PluginVersionConstraint::of)
                .collect(Collectors.toSet());
    }

    @Override
    public NativeEntity<CacheDto> createNativeEntity(Entity entity,
                                                     Map<String, ValueReference> parameters,
                                                     Map<EntityDescriptor, Object> nativeEntities,
                                                     String username) {
        if (entity instanceof EntityV1) {
            return decode((EntityV1) entity, parameters);
        } else {
            throw new IllegalArgumentException("Unsupported entity version: " + entity.getClass());
        }
    }

    private NativeEntity<CacheDto> decode(EntityV1 entity, Map<String, ValueReference> parameters) {
        final LookupCacheEntity lookupCacheEntity = objectMapper.convertValue(entity.data(), LookupCacheEntity.class);
        final LookupCacheConfiguration configuration = objectMapper.convertValue(toValueMap(lookupCacheEntity.configuration(), parameters), LookupCacheConfiguration.class);
        final CacheDto cacheDto = CacheDto.builder()
                .name(lookupCacheEntity.name().asString(parameters))
                .title(lookupCacheEntity.title().asString(parameters))
                .description(lookupCacheEntity.description().asString(parameters))
                .config(configuration)
                .build();

        final CacheDto savedCacheDto = cacheService.save(cacheDto);
        return NativeEntity.create(entity.id(), savedCacheDto.id(), TYPE_V1, savedCacheDto.title(), savedCacheDto);
    }

    @Override
    public Optional<NativeEntity<CacheDto>> findExisting(Entity entity, Map<String, ValueReference> parameters) {
        if (entity instanceof EntityV1) {
            return findExisting((EntityV1) entity, parameters);
        } else {
            throw new IllegalArgumentException("Unsupported entity version: " + entity.getClass());
        }
    }

    private Optional<NativeEntity<CacheDto>> findExisting(EntityV1 entity, Map<String, ValueReference> parameters) {
        final LookupCacheEntity cacheEntity = objectMapper.convertValue(entity.data(), LookupCacheEntity.class);
        final String name = cacheEntity.name().asString(parameters);

        final Optional<CacheDto> existingCache = cacheService.get(name);

        return existingCache.map(cache -> NativeEntity.create(entity.id(), cache.id(), TYPE_V1, cache.title(), cache));
    }

    @Override
    public Optional<NativeEntity<CacheDto>> loadNativeEntity(NativeEntityDescriptor nativeEntityDescriptor) {
        return cacheService.get(nativeEntityDescriptor.id().id())
                .map(entity -> NativeEntity.create(nativeEntityDescriptor, entity));
    }

    @Override
    public void delete(CacheDto nativeEntity) {
        cacheService.delete(nativeEntity.id());
    }

    @Override
    public EntityExcerpt createExcerpt(CacheDto cacheDto) {
        return EntityExcerpt.builder()
                .id(ModelId.of(cacheDto.id()))
                .type(ModelTypes.LOOKUP_CACHE_V1)
                .title(cacheDto.title())
                .build();
    }

    @Override
    public Set<EntityExcerpt> listEntityExcerpts() {
        return cacheService.findAll().stream()
                .map(this::createExcerpt)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<Entity> exportEntity(EntityDescriptor entityDescriptor, EntityDescriptorIds entityDescriptorIds) {
        final ModelId modelId = entityDescriptor.id();
        return cacheService.get(modelId.id()).map(cacheDto -> exportNativeEntity(cacheDto, entityDescriptorIds));
    }
}
