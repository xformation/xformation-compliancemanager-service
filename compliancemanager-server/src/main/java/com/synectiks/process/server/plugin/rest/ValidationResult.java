/*
 * */
package com.synectiks.process.server.plugin.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Map;

@JsonAutoDetect
public class ValidationResult {

    private final Multimap<String, String> errors = ArrayListMultimap.create();
    private final Multimap<String, String> context = ArrayListMultimap.create();


    public void addError(String fieldName, String error) {
        errors.put(fieldName, error);
    }
    public void addContext(String fieldName, Iterable<String> values) {
        context.putAll(fieldName, values);
    }

    public void addAll(Multimap<String, String> extraErrors) {
        errors.putAll(extraErrors);
    }

    public void addAll(ValidationResult validationResult) {
        errors.putAll(validationResult.errors);
    }

    @JsonProperty("failed")
    public boolean failed() {
        return !errors.isEmpty();
    }

    @JsonProperty("errors")
    public Map<String, Collection<String>> getErrors() {
        return errors.asMap();
    }

    @JsonProperty("error_context")
    public Map<String, Collection<String>> getContext() {
        return context.asMap();
    }
}
