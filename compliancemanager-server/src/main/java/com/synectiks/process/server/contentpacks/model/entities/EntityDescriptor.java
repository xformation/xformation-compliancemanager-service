/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.Identified;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelType;
import com.synectiks.process.server.contentpacks.model.Typed;

/**
 * The unique description of a (virtual) entity by ID and type.
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_EntityDescriptor.Builder.class)
public abstract class EntityDescriptor implements Identified, Typed {
    public static EntityDescriptor create(ModelId id, ModelType type) {
        return builder()
                .id(id)
                .type(type)
                .build();
    }

    /**
     * Shortcut for {@link #create(ModelId, ModelType)}
     */
    public static EntityDescriptor create(String id, ModelType type) {
        return create(ModelId.of(id), type);
    }

    public static Builder builder() {
        return new AutoValue_EntityDescriptor.Builder();
    }

    @AutoValue.Builder
    public interface Builder extends IdBuilder<Builder>, TypeBuilder<Builder> {
        EntityDescriptor build();
    }
}
