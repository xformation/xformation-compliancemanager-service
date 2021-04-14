/*
 * */
package com.synectiks.process.server.indexer.fieldtypes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = FieldTypeDTO.Builder.class)
public abstract class FieldTypeDTO {
    static final String FIELD_NAME = "field_name";
    static final String FIELD_PHYSICAL_TYPE = "physical_type";

    @JsonProperty(FIELD_NAME)
    public abstract String fieldName();

    @JsonProperty(FIELD_PHYSICAL_TYPE)
    public abstract String physicalType();

    public static FieldTypeDTO create(String fieldName, String physicalType) {
        return builder().fieldName(fieldName).physicalType(physicalType).build();
    }

    public static Builder builder() {
        return Builder.create();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public static abstract class Builder {
        @JsonCreator
        public static Builder create() {
            return new AutoValue_FieldTypeDTO.Builder();
        }

        @JsonProperty(FIELD_NAME)
        public abstract Builder fieldName(String fieldName);

        @JsonProperty(FIELD_PHYSICAL_TYPE)
        public abstract Builder physicalType(String physicalType);

        public abstract FieldTypeDTO build();
    }
}