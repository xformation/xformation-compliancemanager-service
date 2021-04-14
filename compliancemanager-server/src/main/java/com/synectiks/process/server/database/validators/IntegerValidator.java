/*
 * */
package com.synectiks.process.server.database.validators;

import com.synectiks.process.server.plugin.database.validators.ValidationResult;
import com.synectiks.process.server.plugin.database.validators.Validator;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public class IntegerValidator implements Validator {
    /**
     * Validates: Object is not null and of type Integer.
     *
     *
     * @param value The object to check
     * @return validation result
     */
    @Override
    public ValidationResult validate(Object value) {
        if (value != null && value instanceof Integer)
            return new ValidationResult.ValidationPassed();
        else
            return new ValidationResult.ValidationFailed("Value \"" + value + "\" is not a valid number!");
    }
}
