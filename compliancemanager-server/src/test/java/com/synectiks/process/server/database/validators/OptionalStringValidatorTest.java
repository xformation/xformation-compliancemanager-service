/*
 * */
package com.synectiks.process.server.database.validators;

import org.junit.Test;

import com.synectiks.process.server.database.validators.OptionalStringValidator;
import com.synectiks.process.server.plugin.database.validators.ValidationResult;

import static org.assertj.core.api.Assertions.assertThat;

public class OptionalStringValidatorTest {
    private OptionalStringValidator validator = new OptionalStringValidator();

    @Test
    public void validateNull() {
        assertThat(validator.validate(null)).isInstanceOf(ValidationResult.ValidationPassed.class);
    }

    @Test
    public void validateEmptyString() {
        assertThat(validator.validate("")).isInstanceOf(ValidationResult.ValidationPassed.class);
    }

    @Test
    public void validateString() {
        assertThat(validator.validate("foobar")).isInstanceOf(ValidationResult.ValidationPassed.class);
    }

    @Test
    public void validateNonString() {
        assertThat(validator.validate(new Object())).isInstanceOf(ValidationResult.ValidationFailed.class);
    }
}