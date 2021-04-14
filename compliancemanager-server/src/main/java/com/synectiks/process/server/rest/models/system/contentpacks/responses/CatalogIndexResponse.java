/*
 * */
package com.synectiks.process.server.rest.models.system.contentpacks.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.entities.EntityExcerpt;

import java.util.Set;

@JsonAutoDetect
@AutoValue
public abstract class CatalogIndexResponse {
    @JsonProperty("entities")
    public abstract Set<EntityExcerpt> entities();

    @JsonCreator
    public static CatalogIndexResponse create(@JsonProperty("entities") Set<EntityExcerpt> entities) {
        return new AutoValue_CatalogIndexResponse(entities);
    }
}
