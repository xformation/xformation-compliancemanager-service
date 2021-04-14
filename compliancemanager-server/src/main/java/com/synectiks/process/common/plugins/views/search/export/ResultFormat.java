/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import static com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand.DEFAULT_FIELDS;
import static com.synectiks.process.common.plugins.views.search.export.LinkedHashSetUtil.linkedHashSetOf;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.OptionalInt;

@JsonAutoDetect
@AutoValue
@JsonDeserialize(builder = ResultFormat.Builder.class)
public abstract class ResultFormat {
    private static final String FIELD_FIELDS = "fields_in_order";

    @JsonProperty(FIELD_FIELDS)
    @NotEmpty
    public abstract LinkedHashSet<String> fieldsInOrder();

    @JsonProperty
    @Positive
    public abstract OptionalInt limit();

    @JsonProperty
    public abstract Map<String, Object> executionState();

    public static ResultFormat.Builder builder() {
        return ResultFormat.Builder.create();
    }

    public static ResultFormat empty() {
        return ResultFormat.builder().build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty(FIELD_FIELDS)
        public abstract Builder fieldsInOrder(LinkedHashSet<String> fieldsInOrder);

        public Builder fieldsInOrder(String... fields) {
            return fieldsInOrder(linkedHashSetOf(fields));
        }

        @JsonProperty
        public abstract Builder limit(Integer limit);

        @JsonProperty
        public abstract Builder executionState(Map<String, Object> executionState);

        abstract ResultFormat autoBuild();

        public ResultFormat build() {
            return autoBuild();
        }

        @JsonCreator
        public static ResultFormat.Builder create() {
            return new AutoValue_ResultFormat.Builder()
                    .fieldsInOrder(DEFAULT_FIELDS)
                    .executionState(Collections.emptyMap());
        }
    }
}
