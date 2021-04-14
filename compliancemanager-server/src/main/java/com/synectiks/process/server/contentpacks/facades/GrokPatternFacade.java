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
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.contentpacks.exceptions.DivergingEntityConfigurationException;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelType;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.entities.Entity;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.EntityExcerpt;
import com.synectiks.process.server.contentpacks.model.entities.EntityV1;
import com.synectiks.process.server.contentpacks.model.entities.GrokPatternEntity;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntity;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.grok.GrokPattern;
import com.synectiks.process.server.grok.GrokPatternService;
import com.synectiks.process.server.plugin.database.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GrokPatternFacade implements EntityFacade<GrokPattern> {
    private static final Logger LOG = LoggerFactory.getLogger(GrokPatternFacade.class);

    public static final ModelType TYPE_V1 = ModelTypes.GROK_PATTERN_V1;

    private final ObjectMapper objectMapper;
    private final GrokPatternService grokPatternService;

    @Inject
    public GrokPatternFacade(ObjectMapper objectMapper, GrokPatternService grokPatternService) {
        this.objectMapper = objectMapper;
        this.grokPatternService = grokPatternService;
    }

    @VisibleForTesting
    Entity exportNativeEntity(GrokPattern grokPattern, EntityDescriptorIds entityDescriptorIds) {
        final GrokPatternEntity grokPatternEntity = GrokPatternEntity.create(grokPattern.name(), grokPattern.pattern());
        final JsonNode data = objectMapper.convertValue(grokPatternEntity, JsonNode.class);
        return EntityV1.builder()
                .id(ModelId.of(entityDescriptorIds.getOrThrow(grokPattern.id(), ModelTypes.GROK_PATTERN_V1)))
                .type(ModelTypes.GROK_PATTERN_V1)
                .data(data)
                .build();
    }

    @Override
    public NativeEntity<GrokPattern> createNativeEntity(Entity entity,
                                                        Map<String, ValueReference> parameters,
                                                        Map<EntityDescriptor, Object> nativeEntities,
                                                        String username) {
        if (entity instanceof EntityV1) {
            return decode((EntityV1) entity);
        } else {
            throw new IllegalArgumentException("Unsupported entity version: " + entity.getClass());
        }
    }

    private NativeEntity<GrokPattern> decode(EntityV1 entity) {
        final GrokPatternEntity grokPatternEntity = objectMapper.convertValue(entity.data(), GrokPatternEntity.class);

        final GrokPattern grokPattern = GrokPattern.create(grokPatternEntity.name(), grokPatternEntity.pattern());
        try {
            final GrokPattern savedGrokPattern = grokPatternService.save(grokPattern);
            return NativeEntity.create(entity.id(), savedGrokPattern.id(), TYPE_V1, savedGrokPattern.name(), savedGrokPattern);
        } catch (ValidationException e) {
            throw new RuntimeException("Couldn't create grok pattern " + grokPattern.name());
        }
    }

    @Override
    public Optional<NativeEntity<GrokPattern>> loadNativeEntity(NativeEntityDescriptor nativeEntityDescriptor) {
        try {
            final GrokPattern grokPattern = grokPatternService.load(nativeEntityDescriptor.id().id());
            return Optional.of(NativeEntity.create(nativeEntityDescriptor, grokPattern));
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(GrokPattern nativeEntity) {
        grokPatternService.delete(nativeEntity.id());
    }

    @Override
    public Optional<NativeEntity<GrokPattern>> findExisting(Entity entity, Map<String, ValueReference> parameters) {
        if (entity instanceof EntityV1) {
            return findExisting((EntityV1) entity);
        } else {
            throw new IllegalArgumentException("Unsupported entity version: " + entity.getClass());
        }
    }

    private Optional<NativeEntity<GrokPattern>> findExisting(EntityV1 entity) {
        final GrokPatternEntity grokPatternEntity = objectMapper.convertValue(entity.data(), GrokPatternEntity.class);
        final String name = grokPatternEntity.name();
        final String pattern = grokPatternEntity.pattern();

        final Optional<GrokPattern> grokPattern = grokPatternService.loadByName(name);
        grokPattern.ifPresent(existingPattern -> compareGrokPatterns(name, pattern, existingPattern.pattern()));

        return grokPattern.map(gp -> NativeEntity.create(entity.id(), gp.id(), TYPE_V1,gp.name(), gp));
    }

    private void compareGrokPatterns(String name, String expectedPattern, String actualPattern) {
        if (!actualPattern.equals(expectedPattern)) {
            throw new DivergingEntityConfigurationException("Expected Grok pattern for name \"" + name + "\": <" + expectedPattern + ">; actual Grok pattern: <" + actualPattern + ">");
        }
    }

    @Override
    public EntityExcerpt createExcerpt(GrokPattern grokPattern) {
        return EntityExcerpt.builder()
                .id(ModelId.of(grokPattern.id()))
                .type(ModelTypes.GROK_PATTERN_V1)
                .title(grokPattern.name())
                .build();
    }

    @Override
    public Set<EntityExcerpt> listEntityExcerpts() {
        return grokPatternService.loadAll().stream()
                .map(this::createExcerpt)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<Entity> exportEntity(EntityDescriptor entityDescriptor, EntityDescriptorIds entityDescriptorIds) {
        final ModelId modelId = entityDescriptor.id();
        try {
            final GrokPattern grokPattern = grokPatternService.load(modelId.id());
            return Optional.of(exportNativeEntity(grokPattern, entityDescriptorIds));
        } catch (NotFoundException e) {
            LOG.debug("Couldn't find grok pattern {}", entityDescriptor, e);
            return Optional.empty();
        }
    }

    @Override
    public Graph<EntityDescriptor> resolveNativeEntity(EntityDescriptor entityDescriptor) {
        final MutableGraph<EntityDescriptor> mutableGraph = GraphBuilder.directed().build();
        mutableGraph.addNode(entityDescriptor);

        final ModelId modelId = entityDescriptor.id();
        try {
            final GrokPattern grokPattern = grokPatternService.load(modelId.id());

            final String namedPattern = grokPattern.pattern();
            final Set<String> patterns = GrokPatternService.extractPatternNames(namedPattern);
            patterns.stream().forEach(patternName -> {
                grokPatternService.loadByName(patternName).ifPresent(depPattern -> {
                    final EntityDescriptor depEntityDescriptor = EntityDescriptor.create(
                            depPattern.id(), ModelTypes.GROK_PATTERN_V1);
                    mutableGraph.putEdge(entityDescriptor, depEntityDescriptor);
                });
            });
        } catch (NotFoundException e) {
            LOG.debug("Couldn't find grok pattern {}", entityDescriptor, e);
        }
        return mutableGraph;
    }

    @Override
    public Graph<Entity> resolveForInstallation(Entity entity,
                                                Map<String, ValueReference> parameters,
                                                Map<EntityDescriptor, Entity> entities) {
        if (entity instanceof EntityV1) {
            return resolveForInstallationV1((EntityV1) entity, entities);
        } else {
            throw new IllegalArgumentException("Unsupported entity version: " + entity.getClass());
        }
    }

    private Graph<Entity> resolveForInstallationV1(EntityV1 entity,
                                                   Map<EntityDescriptor, Entity> entities) {
        final MutableGraph<Entity> mutableGraph = GraphBuilder.directed().build();
        mutableGraph.addNode(entity);

        final GrokPatternEntity grokPatternEntity = objectMapper.convertValue(entity.data(), GrokPatternEntity.class);
        final String namedPattern = grokPatternEntity.pattern();
        final Set<String> patterns = GrokPatternService.extractPatternNames(namedPattern);
        patterns.stream().forEach(patternName -> {
            entities.entrySet().stream()
                    .filter(x -> x.getValue().type().equals(ModelTypes.GROK_PATTERN_V1))
                    .filter(x -> {
                        EntityV1 entityV1 = (EntityV1) x.getValue();
                        GrokPatternEntity grokPatternEntity1 = objectMapper.convertValue(entityV1.data(), GrokPatternEntity.class);
                        return grokPatternEntity1.name().equals(patternName);
                    }).forEach(x -> mutableGraph.putEdge(entity, x.getValue()));
        });

        return ImmutableGraph.copyOf(mutableGraph);
    }
}
