/*
 * */
package com.synectiks.process.server.contentpacks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface Revisioned {
    String FIELD_META_REVISION = "rev";

    @JsonProperty(FIELD_META_REVISION)
    int revision();

    interface RevisionBuilder<SELF> {
        @JsonProperty(FIELD_META_REVISION)
        SELF revision(int revision);
    }
}
