/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_ValueType.Builder.class)
public abstract class ValueType implements Parent {
    static final String VERSION = "1";
    private static final String FIELD_FOOBAR = "foobar";

    @JsonProperty(FIELD_FOOBAR)
    public abstract String foobar();

    public static Builder builder() {
        return new AutoValue_ValueType.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder implements Parent.ParentBuilder<Builder> {
        @JsonProperty(FIELD_FOOBAR)
        public abstract Builder foobar(String foobar);

        abstract ValueType autoBuild();

        public ValueType build() {
            version(VERSION);
            return autoBuild();
        }
    }
}