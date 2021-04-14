/*
 * */
package com.synectiks.process.server.plugin.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.auto.value.AutoValue;

@JsonAutoDetect
@AutoValue
@JsonTypeName("ApiError") // Explicitly indicates the class type to avoid AutoValue_ at the beginning
public abstract class ApiError implements GenericError {
    public static ApiError create(String message) {
        return new AutoValue_ApiError(message);
    }
}
