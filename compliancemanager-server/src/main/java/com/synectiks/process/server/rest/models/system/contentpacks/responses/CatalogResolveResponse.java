/*
 * */
package com.synectiks.process.server.rest.models.system.contentpacks.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.contentpacks.model.entities.Entity;

import java.util.Collection;

@JsonAutoDetect
@AutoValue
public abstract class CatalogResolveResponse {
    @JsonProperty("entities")
    public abstract ImmutableSet<Entity> entities();

    @JsonCreator
    public static CatalogResolveResponse create(@JsonProperty("entities") Collection<Entity> entities) {
        return new AutoValue_CatalogResolveResponse(ImmutableSet.copyOf(entities));
    }
}
