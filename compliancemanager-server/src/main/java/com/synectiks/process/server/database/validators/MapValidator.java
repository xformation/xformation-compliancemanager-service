/*
 * */
package com.synectiks.process.server.database.validators;

import java.util.Map;

import com.synectiks.process.server.plugin.database.validators.ValidationResult;
import com.synectiks.process.server.plugin.database.validators.Validator;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class MapValidator implements Validator {

    @Override
    public ValidationResult validate(Object value) {
        if (value instanceof Map)
            return new ValidationResult.ValidationPassed();
        else
            return new ValidationResult.ValidationFailed("Value is not a Map!");
    }

}
