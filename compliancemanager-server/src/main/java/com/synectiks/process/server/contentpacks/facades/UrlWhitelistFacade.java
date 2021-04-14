/*
 * */
package com.synectiks.process.server.contentpacks.facades;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelType;
import com.synectiks.process.server.contentpacks.model.constraints.GraylogVersionConstraint;
import com.synectiks.process.server.contentpacks.model.entities.Entity;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.EntityExcerpt;
import com.synectiks.process.server.contentpacks.model.entities.EntityV1;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntity;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.system.urlwhitelist.UrlWhitelistService;
import com.synectiks.process.server.system.urlwhitelist.WhitelistEntry;

import javax.inject.Inject;

import static com.synectiks.process.server.contentpacks.model.ModelTypes.URL_WHITELIST_ENTRY_V1;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class UrlWhitelistFacade implements EntityFacade<WhitelistEntry> {
    public static final ModelType TYPE_V1 = URL_WHITELIST_ENTRY_V1;

    private final ObjectMapper objectMapper;
    private final UrlWhitelistService urlWhitelistService;

    @Inject
    public UrlWhitelistFacade(ObjectMapper objectMapper, UrlWhitelistService urlWhitelistService) {
        this.objectMapper = objectMapper;
        this.urlWhitelistService = urlWhitelistService;
    }

    @Override
    public Optional<Entity> exportEntity(EntityDescriptor entityDescriptor, EntityDescriptorIds entityDescriptorIds) {
        final ModelId modelId = entityDescriptor.id();

        return urlWhitelistService.getEntry(modelId.id())
                .map(entry -> EntityV1.builder()
                        .id(ModelId.of(entityDescriptorIds.getOrThrow(entry.id(), URL_WHITELIST_ENTRY_V1)))
                        .type(URL_WHITELIST_ENTRY_V1)
                        .data(objectMapper.convertValue(entry, JsonNode.class))
                        .constraints(ImmutableSet.of(GraylogVersionConstraint.of(Version.from(3, 1, 3))))
                        .build());
    }

    @Override
    public NativeEntity<WhitelistEntry> createNativeEntity(Entity entity, Map<String, ValueReference> parameters,
            Map<EntityDescriptor, Object> nativeEntities, String username) {

        if (!(entity instanceof EntityV1)) {
            throw new IllegalArgumentException("Unsupported entity version: " + entity.getClass());
        }

        final WhitelistEntry whitelistEntry =
                objectMapper.convertValue(((EntityV1) entity).data(), WhitelistEntry.class);

        urlWhitelistService.addEntry(whitelistEntry);

        return NativeEntity.create(entity.id(), whitelistEntry.id(), TYPE_V1, createTitle(whitelistEntry),
                whitelistEntry);
    }

    @Override
    public Optional<NativeEntity<WhitelistEntry>> loadNativeEntity(NativeEntityDescriptor nativeEntityDescriptor) {
        final ModelId modelId = nativeEntityDescriptor.id();
        return urlWhitelistService.getEntry(modelId.id())
                .map(entry -> NativeEntity.create(nativeEntityDescriptor, entry));
    }

    @Override
    public void delete(WhitelistEntry nativeEntity) {
        urlWhitelistService.removeEntry(nativeEntity.id());
    }

    @Override
    public EntityExcerpt createExcerpt(WhitelistEntry whitelistEntry) {
        return EntityExcerpt.builder()
                .id(ModelId.of(whitelistEntry.id()))
                .type(URL_WHITELIST_ENTRY_V1)
                .title(createTitle(whitelistEntry))
                .build();
    }

    @Override
    public Set<EntityExcerpt> listEntityExcerpts() {
        return urlWhitelistService.getWhitelist()
                .entries()
                .stream()
                .map(this::createExcerpt)
                .collect(Collectors.toSet());
    }

    private String createTitle(WhitelistEntry entry) {
        return entry.title() + " [" + entry.value() + "]";
    }
}
