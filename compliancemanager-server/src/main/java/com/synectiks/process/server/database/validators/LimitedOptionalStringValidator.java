/*
 * */
package com.synectiks.process.server.database.validators;

import static com.google.common.base.Preconditions.checkArgument;

import com.synectiks.process.server.plugin.database.validators.ValidationResult;

public class LimitedOptionalStringValidator extends OptionalStringValidator {
    private final int maxLength;

    public LimitedOptionalStringValidator(int maxLength) {
        checkArgument(maxLength > 0, "maxLength must be greater than 0");
        this.maxLength = maxLength;
    }

    @Override
    public ValidationResult validate(Object value) {
        ValidationResult result = super.validate(value);

        if (result instanceof ValidationResult.ValidationPassed) {
            final String sValue = (String) value;
            if (sValue != null && sValue.length() > maxLength) {
                result = new ValidationResult.ValidationFailed("Value is longer than " + maxLength + " characters!");
            }
        }

        return result;
    }
}
