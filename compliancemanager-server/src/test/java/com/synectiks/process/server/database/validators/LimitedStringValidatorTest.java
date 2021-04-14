/*
 * */
package com.synectiks.process.server.database.validators;

import org.junit.Test;

import com.synectiks.process.server.database.validators.LimitedStringValidator;
import com.synectiks.process.server.plugin.database.validators.ValidationResult;

import static org.assertj.core.api.Assertions.assertThat;

public class LimitedStringValidatorTest {
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeMinLength() {
        new LimitedStringValidator(-1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeMaxLength() {
        new LimitedStringValidator(1, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroMinLength() {
        new LimitedStringValidator(0, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroMaxLength() {
        new LimitedStringValidator(1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMinLengthGreaterThanMaxLength() {
        new LimitedStringValidator(10, 1);
    }

    @Test
    public void testValidateNullValue() {
        assertThat(new LimitedStringValidator(1, 1).validate(null))
                .isInstanceOf(ValidationResult.ValidationFailed.class);
    }

    @Test
    public void testValidateEmptyValue() {
        assertThat(new LimitedStringValidator(1, 1).validate(""))
                .isInstanceOf(ValidationResult.ValidationFailed.class);
    }

    @Test
    public void testValidateLongString() {
        assertThat(new LimitedStringValidator(1, 1).validate("12"))
                .isInstanceOf(ValidationResult.ValidationFailed.class);
    }

    @Test
    public void testValidateShortString() {
        assertThat(new LimitedStringValidator(2, 2).validate("1"))
                .isInstanceOf(ValidationResult.ValidationFailed.class);
    }

    @Test
    public void testValidateNoString() {
        assertThat(new LimitedStringValidator(1, 1).validate(123))
                .isInstanceOf(ValidationResult.ValidationFailed.class);
    }

    @Test
    public void testValidateValidString() {
        assertThat(new LimitedStringValidator(1, 5).validate("test"))
                .isInstanceOf(ValidationResult.ValidationPassed.class);
    }

    @Test
    public void testValidateMinLengthString() {
        assertThat(new LimitedStringValidator(1, 5).validate("1"))
                .isInstanceOf(ValidationResult.ValidationPassed.class);
    }

    @Test
    public void testValidateMaxLengthString() {
        assertThat(new LimitedStringValidator(1, 5).validate("12345"))
                .isInstanceOf(ValidationResult.ValidationPassed.class);
    }
}