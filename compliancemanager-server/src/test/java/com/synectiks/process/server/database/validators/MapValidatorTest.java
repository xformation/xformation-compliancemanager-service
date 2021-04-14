/*
 * */
package com.synectiks.process.server.database.validators;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.database.validators.MapValidator;
import com.synectiks.process.server.plugin.database.validators.Validator;

import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MapValidatorTest {

    @Test
    public void testValidate() throws Exception {
        Validator v = new MapValidator();

        assertFalse(v.validate(null).passed());
        assertFalse(v.validate(Collections.emptyList()).passed());
        assertFalse(v.validate(9001).passed());
        assertFalse(v.validate("foo").passed());

        Map<String, String> actuallyFilledMap = ImmutableMap.of(
                "foo", "bar",
                "lol", "wut");

        assertTrue(v.validate(actuallyFilledMap).passed());
        assertTrue(v.validate(Collections.emptyMap()).passed());
    }

}
