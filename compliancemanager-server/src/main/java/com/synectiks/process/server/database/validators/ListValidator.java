/*
 * */
package com.synectiks.process.server.database.validators;

import java.util.List;

import com.synectiks.process.server.plugin.database.validators.ValidationResult;
import com.synectiks.process.server.plugin.database.validators.Validator;

public class ListValidator implements Validator {
    private boolean allowMissing;

    public ListValidator() {
        this(false);
    }

    public ListValidator(boolean allowNull) {
        this.allowMissing = allowNull;
    }

    @Override
    public ValidationResult validate(Object value) {
        if ((allowMissing && value == null) || value instanceof List) {
            return new ValidationResult.ValidationPassed();
        } else {
            return new ValidationResult.ValidationFailed("Value is not a list!");
        }
    }
}
