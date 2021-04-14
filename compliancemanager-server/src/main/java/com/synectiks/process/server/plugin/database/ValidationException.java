/*
 * */
package com.synectiks.process.server.plugin.database;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.plugin.database.validators.ValidationResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ValidationException extends Exception {
    private final Map<String, List<ValidationResult>> errors;

    public ValidationException(Map<String, List<ValidationResult>> errors) {
        this.errors = ImmutableMap.copyOf(errors);
    }

    public ValidationException(final String message) {
        this("_", message);
    }

    public ValidationException(final String field, final String message) {
        super(message);
        this.errors = ImmutableMap.of(field, Collections.<ValidationResult>singletonList(new ValidationResult.ValidationFailed(message)));
    }

    public Map<String, List<ValidationResult>> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("message", getLocalizedMessage())
            .add("errors", errors)
            .toString();
    }
}
