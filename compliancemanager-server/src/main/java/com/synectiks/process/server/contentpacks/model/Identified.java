/*
 * */
package com.synectiks.process.server.contentpacks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface Identified {
    String FIELD_META_ID = "id";

    @JsonProperty(FIELD_META_ID)
    ModelId id();

    interface IdBuilder<SELF> {
        ModelId id();

        @JsonProperty(FIELD_META_ID)
        SELF id(ModelId id);
    }
}
