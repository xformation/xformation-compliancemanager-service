/*
 * */
package com.synectiks.process.server.contentpacks;

import com.google.common.graph.MutableGraph;
import com.synectiks.process.server.contentpacks.model.entities.Entity;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.EntityV1;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;

import java.util.Map;

public interface NativeEntityConverter<T> {
    T toNativeEntity(Map<String, ValueReference> parameters, Map<EntityDescriptor, Object> nativeEntities);
    default void resolveForInstallation(EntityV1 entity,
                                        Map<String, ValueReference> parameters,
                                        Map<EntityDescriptor, Entity> entities,
                                        MutableGraph<Entity> graph) {

    }
}
