/*
 * */
package com.synectiks.process.server.contentpacks.model.parameters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueType;

@AutoValue
@JsonDeserialize(builder = AutoValue_BooleanParameter.Builder.class)
public abstract class BooleanParameter implements Parameter<Boolean> {
    static final String TYPE_NAME = "boolean";

    public static Builder builder() {
        return new AutoValue_BooleanParameter.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder implements ParameterBuilder<Builder, Boolean> {
        abstract BooleanParameter autoBuild();

        public BooleanParameter build() {
            valueType(ValueType.BOOLEAN);
            return autoBuild();
        }
    }
}
