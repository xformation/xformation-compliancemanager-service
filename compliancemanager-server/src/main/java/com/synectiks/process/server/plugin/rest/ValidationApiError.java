/*
 * */
package com.synectiks.process.server.plugin.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.plugin.database.validators.ValidationResult;

import java.util.List;
import java.util.Map;

@JsonAutoDetect
@AutoValue
@JsonTypeName("ValidationApiError") // Explicitly indicates the class type to avoid AutoValue_ at the beginning
public abstract class ValidationApiError implements GenericError {
    @JsonProperty
    public abstract Map<String, List<ValidationResult>> validationErrors();

    public static ValidationApiError create(String message, Map<String, List<ValidationResult>> validationErrors) {
        return new AutoValue_ValidationApiError(message, ImmutableMap.copyOf(validationErrors));
    }
}
