/*
 * */
package com.synectiks.process.server.database.validators;

import com.synectiks.process.server.plugin.database.validators.ValidationResult;
import com.synectiks.process.server.plugin.database.validators.Validator;

public class OptionalStringValidator implements Validator {
    /**
     * Validates: Object is null or of type String.
     *
     * @param value The object to check
     * @return validation result
     */
    @Override
    public ValidationResult validate(Object value) {
        if (value == null || value instanceof String) {
            return new ValidationResult.ValidationPassed();
        } else {
            return new ValidationResult.ValidationFailed("Value \"" + value + "\" is not a valid string!");
        }
    }
}
