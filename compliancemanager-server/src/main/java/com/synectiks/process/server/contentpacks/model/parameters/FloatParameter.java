/*
 * */
package com.synectiks.process.server.contentpacks.model.parameters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueType;

@AutoValue
@JsonDeserialize(builder = AutoValue_FloatParameter.Builder.class)
public abstract class FloatParameter implements Parameter<Float> {
    static final String TYPE_NAME = "float";

    public static Builder builder() {
        return new AutoValue_FloatParameter.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder implements ParameterBuilder<Builder, Float> {
        abstract FloatParameter autoBuild();

        public FloatParameter build() {
            valueType(ValueType.FLOAT);
            return autoBuild();
        }
    }
}
