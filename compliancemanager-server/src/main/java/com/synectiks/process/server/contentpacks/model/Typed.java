/*
 * */
package com.synectiks.process.server.contentpacks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface Typed {
    String FIELD_META_TYPE = "type";

    @JsonProperty(FIELD_META_TYPE)
    ModelType type();

    interface TypeBuilder<SELF> {
        @JsonProperty(FIELD_META_TYPE)
        SELF type(ModelType type);
    }
}
