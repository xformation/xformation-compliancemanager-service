/*
 * */
package com.synectiks.process.server.rest.models.system.contentpacks.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;

import java.util.Set;

@JsonAutoDetect
@AutoValue
public abstract class CatalogResolveRequest {
    @JsonProperty("entities")
    public abstract ImmutableSet<EntityDescriptor> entities();

    @JsonCreator
    public static CatalogResolveRequest create(@JsonProperty("entities") Set<EntityDescriptor> entities) {
        return new AutoValue_CatalogResolveRequest(ImmutableSet.copyOf(entities));
    }
}
