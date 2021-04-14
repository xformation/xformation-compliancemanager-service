/*
 * */
package com.synectiks.process.server.database.validators;

import org.junit.Test;

import com.synectiks.process.server.database.validators.LimitedOptionalStringValidator;
import com.synectiks.process.server.plugin.database.validators.ValidationResult;

import static org.assertj.core.api.Assertions.assertThat;

public class LimitedOptionalStringValidatorTest {
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeMaxLength() {
        new LimitedOptionalStringValidator(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroMaxLength() {
        new LimitedOptionalStringValidator(0);
    }

    @Test
    public void testValidateNullValue() {
        assertThat(new LimitedOptionalStringValidator(1).validate(null))
                .isInstanceOf(ValidationResult.ValidationPassed.class);
    }

    @Test
    public void testValidateEmptyValue() {
        assertThat(new LimitedOptionalStringValidator(1).validate(""))
                .isInstanceOf(ValidationResult.ValidationPassed.class);
    }

    @Test
    public void testValidateLongString() {
        assertThat(new LimitedOptionalStringValidator(1).validate("12"))
                .isInstanceOf(ValidationResult.ValidationFailed.class);
    }

    @Test
    public void testValidateNoString() {
        assertThat(new LimitedOptionalStringValidator(1).validate(123))
                .isInstanceOf(ValidationResult.ValidationFailed.class);
    }

    @Test
    public void testValidateMaxLengthString() {
        assertThat(new LimitedOptionalStringValidator(5).validate("12345"))
                .isInstanceOf(ValidationResult.ValidationPassed.class);
    }
}