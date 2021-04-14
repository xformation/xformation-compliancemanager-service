/*
 * */
package com.synectiks.process.server.contentpacks.model.parameters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueType;

@AutoValue
@JsonDeserialize(builder = AutoValue_DoubleParameter.Builder.class)
public abstract class DoubleParameter implements Parameter<Double> {
    static final String TYPE_NAME = "double";

    public static Builder builder() {
        return new AutoValue_DoubleParameter.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder implements ParameterBuilder<Builder, Double> {
        abstract DoubleParameter autoBuild();

        public DoubleParameter build() {
            valueType(ValueType.DOUBLE);
            return autoBuild();
        }
    }
}
