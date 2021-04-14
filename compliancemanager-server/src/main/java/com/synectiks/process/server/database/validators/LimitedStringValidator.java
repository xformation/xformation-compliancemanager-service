/*
 * */
package com.synectiks.process.server.database.validators;

import static com.google.common.base.Preconditions.checkArgument;

import com.synectiks.process.server.plugin.database.validators.ValidationResult;

public class LimitedStringValidator extends FilledStringValidator {
    private final int minLength;
    private final int maxLength;

    public LimitedStringValidator(int minLength, int maxLength) {
        checkArgument(minLength > 0, "minLength must be greater than 0");
        checkArgument(maxLength > 0, "maxLength must be greater than 0");
        checkArgument(minLength <= maxLength, "maxLength must be greater than or equal to minLength");

        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    /**
     * Validates: applies the validation from {@link FilledStringValidator} and also check that value's length
     * is between the minimum and maximum length passed to the constructor.
     *
     * @param value The object to check
     * @return validation result
     */
    @Override
    public ValidationResult validate(Object value) {
        ValidationResult result = super.validate(value);
        if (result instanceof ValidationResult.ValidationPassed) {
            final String sValue = (String)value;
            if (sValue.length() < minLength || sValue.length() > maxLength) {
                result = new ValidationResult.ValidationFailed("Value is not between " + minLength + " and " + maxLength + " in length!");
            }
        }
        return result;
    }
}
