/*
 * */
package com.synectiks.process.server.contentpacks.model.parameters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueType;

@AutoValue
@JsonDeserialize(builder = AutoValue_StringParameter.Builder.class)
public abstract class StringParameter implements Parameter<String> {
    static final String TYPE_NAME = "string";

    public static Builder builder() {
        return new AutoValue_StringParameter.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder implements ParameterBuilder<Builder, String> {
        abstract StringParameter autoBuild();

        public StringParameter build() {
            valueType(ValueType.STRING);
            return autoBuild();
        }
    }
}