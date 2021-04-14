/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.auto.value.AutoValue;

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE, include = JsonTypeInfo.As.PROPERTY, property = Parent.FIELD_VERSION)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ValueType.class, name = ValueType.VERSION),
        @JsonSubTypes.Type(value = AutoValueSubtypeResolverTest.NestedValueType.class, name = AutoValueSubtypeResolverTest.NestedValueType.VERSION)
})
public interface Parent {
    String FIELD_VERSION = "v";
    String FIELD_TEXT = "text";

    @JsonProperty(FIELD_VERSION)
    String version();

    @JsonProperty(FIELD_TEXT)
    String text();

    @AutoValue.Builder
    interface ParentBuilder<SELF> {
        @JsonProperty(FIELD_VERSION)
        SELF version(String version);

        @JsonProperty(FIELD_TEXT)
        SELF text(String text);
    }
}
