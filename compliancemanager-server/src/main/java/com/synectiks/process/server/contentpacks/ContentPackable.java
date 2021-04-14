/*
 * */
package com.synectiks.process.server.contentpacks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.graph.MutableGraph;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;

public interface ContentPackable<T> {
    T toContentPackEntity(EntityDescriptorIds entityDescriptorIds);
    default void resolveNativeEntity(EntityDescriptor entityDescriptor,
                                     MutableGraph<EntityDescriptor> mutableGraph) {
    }

    @JsonIgnore
    default String getContentPackPluginPackage() {
        return this.getClass().getPackage().getName();
    }
}
