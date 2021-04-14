/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface TypedEntity {
    String FIELD_META_TYPE = "type";

    @JsonProperty(FIELD_META_TYPE)
    ModelTypeEntity type();

    default String typeString() {
        return type().type().asString();
    }

    interface TypeBuilder<SELF> {
        @JsonProperty(FIELD_META_TYPE)
        SELF type(ModelTypeEntity type);
    }
}
