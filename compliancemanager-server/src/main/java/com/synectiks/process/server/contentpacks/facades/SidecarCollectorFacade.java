/*
 * */
package com.synectiks.process.server.contentpacks.facades;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.synectiks.process.common.plugins.sidecar.rest.models.Collector;
import com.synectiks.process.common.plugins.sidecar.services.CollectorService;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelType;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.entities.Entity;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.EntityExcerpt;
import com.synectiks.process.server.contentpacks.model.entities.EntityV1;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntity;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.SidecarCollectorEntity;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class SidecarCollectorFacade implements EntityFacade<Collector> {
    private static final Logger LOG = LoggerFactory.getLogger(SidecarCollectorFacade.class);

    public static final ModelType TYPE_V1 = ModelTypes.SIDECAR_COLLECTOR_V1;

    private final ObjectMapper objectMapper;
    private final CollectorService collectorService;

    @Inject
    public SidecarCollectorFacade(ObjectMapper objectMapper, CollectorService collectorService) {
        this.objectMapper = objectMapper;
        this.collectorService = collectorService;
    }

    @VisibleForTesting
    Entity exportNativeEntity(Collector collector, EntityDescriptorIds entityDescriptorIds) {
        final SidecarCollectorEntity collectorEntity = SidecarCollectorEntity.create(
                ValueReference.of(collector.name()),
                ValueReference.of(collector.serviceType()),
                ValueReference.of(collector.nodeOperatingSystem()),
                ValueReference.of(collector.executablePath()),
                ValueReference.of(collector.executeParameters()),
                ValueReference.of(collector.validationParameters()),
                ValueReference.of(collector.defaultTemplate())
        );

        final JsonNode data = objectMapper.convertValue(collectorEntity, JsonNode.class);
        return EntityV1.builder()
                .id(ModelId.of(entityDescriptorIds.getOrThrow(collector.id(), ModelTypes.SIDECAR_COLLECTOR_V1)))
                .type(TYPE_V1)
                .data(data)
                .build();
    }

    @Override
    public NativeEntity<Collector> createNativeEntity(Entity entity,
                                                      Map<String, ValueReference> parameters,
                                                      Map<EntityDescriptor, Object> nativeEntities,
                                                      String username) {
        if (entity instanceof EntityV1) {
            return decode((EntityV1) entity, parameters);
        } else {
            throw new IllegalArgumentException("Unsupported entity version: " + entity.getClass());
        }
    }

    private NativeEntity<Collector> decode(EntityV1 entity, Map<String, ValueReference> parameters) {
        final SidecarCollectorEntity collectorEntity = objectMapper.convertValue(entity.data(), SidecarCollectorEntity.class);

        final Collector collector = Collector.builder()
                .name(collectorEntity.name().asString(parameters))
                .serviceType(collectorEntity.serviceType().asString(parameters))
                .nodeOperatingSystem(collectorEntity.nodeOperatingSystem().asString(parameters))
                .executablePath(collectorEntity.executablePath().asString(parameters))
                .executeParameters(collectorEntity.executeParameters().asString(parameters))
                .validationParameters(collectorEntity.validationParameters().asString(parameters))
                .defaultTemplate(collectorEntity.defaultTemplate().asString(parameters))
                .build();

        final Collector savedCollector = collectorService.save(collector);
        return NativeEntity.create(entity.id(), savedCollector.id(), TYPE_V1, collector.name(), savedCollector);
    }

    @Override
    public Optional<NativeEntity<Collector>> findExisting(Entity entity, Map<String, ValueReference> parameters) {
        if (entity instanceof EntityV1) {
            return findExisting((EntityV1) entity, parameters);
        } else {
            throw new IllegalArgumentException("Unsupported entity version: " + entity.getClass());
        }
    }

    private Optional<NativeEntity<Collector>> findExisting(EntityV1 entity, Map<String, ValueReference> parameters) {
        final SidecarCollectorEntity collectorEntity = objectMapper.convertValue(entity.data(), SidecarCollectorEntity.class);

        final String name = collectorEntity.name().asString(parameters);
        final String os = collectorEntity.nodeOperatingSystem().asString(parameters);
        final Optional<Collector> existingCollector = Optional.ofNullable(collectorService.findByNameAndOs(name, os));

        return existingCollector.map(collector -> NativeEntity.create(entity.id(), collector.id(), TYPE_V1, collector.name(), collector));
    }

    @Override
    public Optional<NativeEntity<Collector>> loadNativeEntity(NativeEntityDescriptor nativeEntityDescriptor) {
        return collectorService.get(nativeEntityDescriptor.id().id())
                .map(entity -> NativeEntity.create(nativeEntityDescriptor, entity));
    }

    @Override
    public void delete(Collector nativeEntity) {
        collectorService.delete(nativeEntity.id());
    }

    @Override
    public EntityExcerpt createExcerpt(Collector collector) {
        return EntityExcerpt.builder()
                .id(ModelId.of(collector.id()))
                .type(TYPE_V1)
                .title(collector.name())
                .build();
    }

    @Override
    public Set<EntityExcerpt> listEntityExcerpts() {
        return collectorService.all().stream()
                .map(this::createExcerpt)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<Entity> exportEntity(EntityDescriptor entityDescriptor, EntityDescriptorIds entityDescriptorIds) {
        final ModelId modelId = entityDescriptor.id();
        final Collector collector = collectorService.find(modelId.id());
        if (isNull(collector)) {
            LOG.debug("Couldn't find collector {}", entityDescriptor);
            return Optional.empty();
        }

        return Optional.of(exportNativeEntity(collector, entityDescriptorIds));
    }
}
