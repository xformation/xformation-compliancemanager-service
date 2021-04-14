/*
 * */
package com.synectiks.process.server.database.validators;

import org.junit.Test;

import com.synectiks.process.server.database.validators.FilledStringValidator;
import com.synectiks.process.server.plugin.database.validators.Validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class FilledStringValidatorTest {

    @Test
    public void testValidate() throws Exception {
        Validator v = new FilledStringValidator();
        assertFalse(v.validate(null).passed());
        assertFalse(v.validate(534).passed());
        assertFalse(v.validate("").passed());
        assertFalse(v.validate(new String()).passed());
        assertTrue(v.validate("so valid").passed());
    }

}
