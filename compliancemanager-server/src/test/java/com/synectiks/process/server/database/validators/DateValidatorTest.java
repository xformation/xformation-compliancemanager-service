/*
 * */
package com.synectiks.process.server.database.validators;

import org.joda.time.DateTimeZone;
import org.junit.Test;

import com.synectiks.process.server.database.validators.DateValidator;
import com.synectiks.process.server.plugin.database.validators.Validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class DateValidatorTest {

    @Test
    public void testValidate() throws Exception {
        Validator v = new DateValidator();

        assertFalse(v.validate(null).passed());
        assertFalse(v.validate(9001).passed());
        assertFalse(v.validate("").passed());
        assertFalse(v.validate(new java.util.Date()).passed());

        // Only joda datetime.
        assertTrue(v.validate(new org.joda.time.DateTime(DateTimeZone.UTC)).passed());

        // Only accepts UTC.
        assertFalse(v.validate(new org.joda.time.DateTime(DateTimeZone.forID("+09:00"))).passed());
    }
}
