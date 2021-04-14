/*
 * */
package com.synectiks.process.server.database.validators;

import org.bson.types.ObjectId;

import com.synectiks.process.server.plugin.database.validators.ValidationResult;
import com.synectiks.process.server.plugin.database.validators.Validator;

import javax.annotation.Nullable;

public class ObjectIdValidator implements Validator {
    /**
     * Validates: Object is not {@code null} and of type {@link ObjectId}.
     *
     * @param value The object to check
     * @return validation result
     */
    @Override
    public ValidationResult validate(@Nullable final Object value) {
        if (value instanceof ObjectId) {
            return new ValidationResult.ValidationPassed();
        } else {
            return new ValidationResult.ValidationFailed(String.valueOf(value) + " is not a valid ObjectId!");
        }
    }
}
