/*
 * */
package com.synectiks.process.common.security.authservice.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@AutoValue
@JsonDeserialize(builder = AuthServiceBackendTestResult.Builder.class)
public abstract class AuthServiceBackendTestResult {
    private static final String FIELD_IS_SUCCESS = "success";
    private static final String FIELD_MESSAGE = "message";
    private static final String FIELD_ERRORS = "errors";
    private static final String FIELD_RESULT = "result";

    @JsonProperty(FIELD_IS_SUCCESS)
    public abstract boolean isSuccess();

    @JsonProperty(FIELD_MESSAGE)
    public abstract String message();

    @JsonProperty(FIELD_ERRORS)
    public abstract ImmutableList<String> errors();

    @JsonProperty(FIELD_RESULT)
    public abstract ImmutableMap<String, Object> result();

    public static AuthServiceBackendTestResult createSuccess(String message) {
        return builder()
                .isSuccess(true)
                .message(message)
                .build();
    }

    public static AuthServiceBackendTestResult createSuccess(String message, Map<String, Object> result) {
        return builder()
                .isSuccess(true)
                .message(message)
                .result(result)
                .build();
    }

    public static AuthServiceBackendTestResult createFailure(String message) {
        return builder()
                .isSuccess(false)
                .message(message)
                .build();
    }

    public static AuthServiceBackendTestResult createFailure(String message, List<String> errors) {
        return builder()
                .isSuccess(false)
                .message(message)
                .errors(errors)
                .build();
    }

    public static AuthServiceBackendTestResult createFailure(String message, Map<String, Object> result) {
        return builder()
                .isSuccess(false)
                .message(message)
                .result(result)
                .build();
    }

    public static AuthServiceBackendTestResult createFailure(String message, List<String> errors, Map<String, Object> result) {
        return builder()
                .isSuccess(false)
                .message(message)
                .errors(errors)
                .result(result)
                .build();
    }

    public static Builder builder() {
        return Builder.create();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonCreator
        public static Builder create() {
            return new AutoValue_AuthServiceBackendTestResult.Builder()
                    .errors(Collections.emptyList())
                    .result(Collections.emptyMap());
        }

        @JsonProperty(FIELD_IS_SUCCESS)
        public abstract Builder isSuccess(boolean isSuccess);

        @JsonProperty(FIELD_MESSAGE)
        public abstract Builder message(String message);

        @JsonProperty(FIELD_ERRORS)
        public abstract Builder errors(List<String> errors);

        @JsonProperty(FIELD_RESULT)
        public abstract Builder result(Map<String, Object> result);

        public abstract AuthServiceBackendTestResult build();
    }
}
