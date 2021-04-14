/*
 * */
package com.synectiks.process.server.plugin.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Collection;

@JsonTypeName("DetailedError") // Explicitly indicates the class type to avoid AutoValue_ at the beginning
public interface DetailedError extends GenericError {
    @JsonProperty
    Collection<String> details();
}
