/*
 * */
package com.synectiks.process.server.plugin.rest;

public class ValidationFailureException extends RuntimeException {
    private final ValidationResult validationResult;

    public ValidationFailureException(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }
}
