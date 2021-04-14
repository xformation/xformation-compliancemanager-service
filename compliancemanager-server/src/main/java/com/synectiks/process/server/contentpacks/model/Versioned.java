/*
 * */
package com.synectiks.process.server.contentpacks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface Versioned {
    String FIELD_META_VERSION = "v";

    @JsonProperty(FIELD_META_VERSION)
    ModelVersion version();

    interface VersionBuilder<SELF> {
        @JsonProperty(FIELD_META_VERSION)
        SELF version(ModelVersion version);
    }
}
