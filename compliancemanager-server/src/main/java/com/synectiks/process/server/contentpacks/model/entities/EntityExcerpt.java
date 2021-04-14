/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.Identified;
import com.synectiks.process.server.contentpacks.model.Typed;

@AutoValue
@JsonDeserialize(builder = AutoValue_EntityExcerpt.Builder.class)
public abstract class EntityExcerpt implements Identified, Typed {
    public static final String FIELD_TITLE = "title";

    @JsonProperty(FIELD_TITLE)
    public abstract String title();

    public static Builder builder() {
        return new AutoValue_EntityExcerpt.Builder();
    }

    @AutoValue.Builder
    public interface Builder extends IdBuilder<Builder>, TypeBuilder<Builder> {
        @JsonProperty(FIELD_TITLE)
        Builder title(String title);

        EntityExcerpt build();
    }
}
