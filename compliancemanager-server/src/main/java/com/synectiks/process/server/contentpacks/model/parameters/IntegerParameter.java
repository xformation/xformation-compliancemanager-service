/*
 * */
package com.synectiks.process.server.contentpacks.model.parameters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueType;

@AutoValue
@JsonDeserialize(builder = AutoValue_IntegerParameter.Builder.class)
public abstract class IntegerParameter implements Parameter<Integer> {
    static final String TYPE_NAME = "integer";

    public static Builder builder() {
        return new AutoValue_IntegerParameter.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder implements ParameterBuilder<Builder, Integer> {
        abstract IntegerParameter autoBuild();

        public IntegerParameter build() {
            valueType(ValueType.INTEGER);
            return autoBuild();
        }
    }
}
