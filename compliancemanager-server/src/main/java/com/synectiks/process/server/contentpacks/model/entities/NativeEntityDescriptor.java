/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.Identified;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelType;
import com.synectiks.process.server.contentpacks.model.Typed;

/**
 * The unique description of a native entity by ID and type.
 */
@AutoValue
@JsonDeserialize(builder = NativeEntityDescriptor.Builder.class)
public abstract class NativeEntityDescriptor implements Identified, Typed {
    public static final String FIELD_ENTITY_ID = "content_pack_entity_id";
    public static final String FIELD_ENTITY_FOUND_ON_SYSTEM = "found_on_system";
    public static final String FIELD_ENTITY_TITLE = "title";

    @JsonProperty(FIELD_ENTITY_ID)
    public abstract ModelId contentPackEntityId();

    @JsonProperty(FIELD_ENTITY_TITLE)
    public abstract String title();

    @JsonProperty(FIELD_ENTITY_FOUND_ON_SYSTEM)
    public abstract boolean foundOnSystem();

    public abstract Builder toBuilder();

    public static NativeEntityDescriptor create(ModelId contentPackEntityId, ModelId id, ModelType type, String title) {
        return builder()
                .contentPackEntityId(contentPackEntityId)
                .id(id)
                .title(title)
                .type(type)
                .build();
    }

    public static NativeEntityDescriptor create(ModelId contentPackEntityId, ModelId id, ModelType type, String title,
                                                boolean foundOnSystem) {
        return builder()
                .contentPackEntityId(contentPackEntityId)
                .id(id)
                .title(title)
                .type(type)
                .foundOnSystem(foundOnSystem)
                .build();
    }

    /**
     * Shortcut for {@link #create(String, String, ModelType, String, boolean)}
     */
    public static NativeEntityDescriptor create(String contentPackEntityId, String nativeId, ModelType type, String title,
                                                boolean foundOnSystem) {
        return create(ModelId.of(contentPackEntityId), ModelId.of(nativeId), type, title, foundOnSystem);
    }

    public static NativeEntityDescriptor create(String contentPackEntityId, String nativeId, ModelType type, String title) {
        return create(ModelId.of(contentPackEntityId), ModelId.of(nativeId), type, title, false);
    }

    public static NativeEntityDescriptor create(ModelId contentPackEntityId, String nativeId, ModelType type, String title,
                                                boolean foundOnSystem) {
        return create(contentPackEntityId, ModelId.of(nativeId), type, title, foundOnSystem);
    }

    public static NativeEntityDescriptor create(ModelId contentPackEntityId, String nativeId, ModelType type, String title) {
        return create(contentPackEntityId, ModelId.of(nativeId), type, title, false);
    }

    public static Builder builder() {
        return NativeEntityDescriptor.Builder.create();
    }

    @AutoValue.Builder
    public abstract static class Builder implements IdBuilder<Builder>, TypeBuilder<Builder> {
        @JsonCreator
        public static Builder create() {
            return new AutoValue_NativeEntityDescriptor.Builder().foundOnSystem(false);
        }

        @JsonProperty(FIELD_ENTITY_ID)
        abstract Builder contentPackEntityId(ModelId contentPackEntityId);

        @JsonProperty(FIELD_ENTITY_TITLE)
        abstract Builder title(String title);

        @JsonProperty(FIELD_ENTITY_FOUND_ON_SYSTEM)
        public abstract Builder foundOnSystem(boolean foundOnSystem);

        public abstract NativeEntityDescriptor build();
    }
}
