/*
 * */
package com.synectiks.process.server.database.validators;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.synectiks.process.server.database.validators.ListValidator;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ListValidatorTest {
    @Test
    public void testValidate() throws Exception {
        final ListValidator v = new ListValidator();

        assertFalse(v.validate(null).passed());
        assertFalse(v.validate(Maps.newHashMap()).passed());
        assertTrue(v.validate(Lists.newArrayList()).passed());
        assertTrue(v.validate(Lists.newArrayList("a", "string")).passed());
    }
}
