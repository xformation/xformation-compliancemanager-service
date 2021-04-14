/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synectiks.process.server.contentpacks.model.Identified;
import com.synectiks.process.server.contentpacks.model.Typed;
import com.synectiks.process.server.contentpacks.model.Versioned;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = Versioned.FIELD_META_VERSION)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EntityV1.class, name = EntityV1.VERSION)
})
public interface Entity extends Identified, Typed, Versioned {
    EntityDescriptor toEntityDescriptor();

    interface EntityBuilder<SELF> extends IdBuilder<SELF>, TypeBuilder<SELF>, VersionBuilder<SELF> {
    }
}
