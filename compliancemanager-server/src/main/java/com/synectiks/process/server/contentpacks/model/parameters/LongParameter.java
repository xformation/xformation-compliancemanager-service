/*
 * */
package com.synectiks.process.server.contentpacks.model.parameters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueType;

@AutoValue
@JsonDeserialize(builder = AutoValue_LongParameter.Builder.class)
public abstract class LongParameter implements Parameter<Long> {
    static final String TYPE_NAME = "long";

    public static Builder builder() {
        return new AutoValue_LongParameter.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder implements ParameterBuilder<Builder, Long> {
        abstract LongParameter autoBuild();

        public LongParameter build() {
            valueType(ValueType.LONG);
            return autoBuild();
        }
    }
}
