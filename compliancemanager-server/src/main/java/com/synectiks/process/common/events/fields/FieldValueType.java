/*
 * */
package com.synectiks.process.common.events.fields;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synectiks.process.common.events.fields.validators.FieldTypeValidator;
import com.synectiks.process.common.events.fields.validators.NoopFieldTypeValidator;

import java.util.List;
import java.util.Optional;

public enum FieldValueType implements FieldTypeValidator {
    @JsonProperty("string")
    STRING(NoopFieldTypeValidator.INSTANCE),
    @JsonProperty("error")
    ERROR(NoopFieldTypeValidator.INSTANCE, true);

    private final FieldTypeValidator validator;
    private final boolean isError;

    FieldValueType(FieldTypeValidator validator) {
        this(validator, false);
    }

    FieldValueType(FieldTypeValidator validator, boolean isError) {
        this.validator = validator;
        this.isError = isError;
    }

    public boolean isError() {
        return isError;
    }

    @Override
    public Optional<List<String>> validate(String value) {
        return validator.validate(value);
    }
}
