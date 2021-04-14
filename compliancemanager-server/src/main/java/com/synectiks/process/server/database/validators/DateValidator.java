/*
 * */
package com.synectiks.process.server.database.validators;

import com.synectiks.process.server.plugin.database.validators.ValidationResult;
import com.synectiks.process.server.plugin.database.validators.Validator;

public class DateValidator implements Validator {
    /**
     * Validates: Object is not null, of type org.joda.time.DateTime and
     * the String representation is in UTC.
     *
     * @param value The object to check
     * @return validation result
     */
    @Override
    public ValidationResult validate(Object value) {
        if (value instanceof org.joda.time.DateTime && value.toString().endsWith("Z")) {
            return new ValidationResult.ValidationPassed();
        } else {
            return new ValidationResult.ValidationFailed(value + " is not a valid date!");
        }
    }
}
