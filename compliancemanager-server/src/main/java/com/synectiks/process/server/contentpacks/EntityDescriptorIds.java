/*
 * */
package com.synectiks.process.server.contentpacks;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.contentpacks.exceptions.ContentPackException;
import com.synectiks.process.server.contentpacks.model.ModelType;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.plugin.streams.Stream;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * This object maps {@link EntityDescriptor} objects to generated IDs that will be used as ID in
 * {@link com.synectiks.process.server.contentpacks.model.entities.Entity} objects.
 */
public class EntityDescriptorIds {
    private final ImmutableMap<EntityDescriptor, String> descriptorIds;

    public static EntityDescriptorIds empty() {
        return new EntityDescriptorIds(ImmutableMap.of());
    }

    public static EntityDescriptorIds of(EntityDescriptor... entityDescriptors) {
        return of(Arrays.asList(entityDescriptors));
    }

    public static EntityDescriptorIds of(Collection<EntityDescriptor> entityDescriptors) {
        final ImmutableMap<EntityDescriptor, String> descriptorIds = entityDescriptors.stream()
                .collect(ImmutableMap.toImmutableMap(Function.identity(), d -> {
                    if (isDefaultStreamDescriptor(d)) {
                        return d.id().id();
                    } else {
                        return UUID.randomUUID().toString();
                    }
                }));

        return new EntityDescriptorIds(descriptorIds);
    }

    public static boolean isDefaultStreamDescriptor(EntityDescriptor descriptor) {
        return ModelTypes.STREAM_V1.equals(descriptor.type()) && Stream.isDefaultStreamId(descriptor.id().id());
    }

    private EntityDescriptorIds(ImmutableMap<EntityDescriptor, String> descriptorIds) {
        this.descriptorIds = descriptorIds;
    }

    public Optional<String> get(final EntityDescriptor descriptor) {
        return Optional.ofNullable(descriptorIds.get(descriptor));
    }

    public Optional<String> get(final String id, final ModelType type) {
        return Optional.ofNullable(descriptorIds.get(EntityDescriptor.create(id, type)));
    }

    public String getOrThrow(EntityDescriptor descriptor) {
        return get(descriptor).orElseThrow(() -> new ContentPackException("Couldn't find entity " + descriptor.toString()));
    }

    public String getOrThrow(final String id, final ModelType type) {
        return get(id, type).orElseThrow(() -> new ContentPackException("Couldn't find entity " + id + "/" + type));
    }
}
