/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelType;

@AutoValue
public abstract class NativeEntity<T> {
    public abstract NativeEntityDescriptor descriptor();

    public abstract T entity();

    public static <T> NativeEntity<T> create(NativeEntityDescriptor entityDescriptor, T entity) {
        return new AutoValue_NativeEntity<>(entityDescriptor, entity);
    }

    /**
     * Shortcut for {@link #create(NativeEntityDescriptor, Object)}
     */
    public static <T> NativeEntity<T> create(String entityId, String nativeId, ModelType type, String title, boolean foundOnSystem, T entity) {
        return create(NativeEntityDescriptor.create(entityId, nativeId, type, title, foundOnSystem), entity);
    }

    public static <T> NativeEntity<T> create(ModelId entityId, String nativeId, ModelType type, String title, boolean foundOnSystem, T entity) {
        return create(NativeEntityDescriptor.create(entityId, nativeId, type, title, foundOnSystem), entity);
    }

    public static <T> NativeEntity<T> create(ModelId entityId, String nativeId, ModelType type, String title, T entity) {
        return create(NativeEntityDescriptor.create(entityId, nativeId, type, title, false), entity);
    }
}
