/*
 * */
package com.synectiks.process.server.inputs.converters;

import org.junit.Test;

import com.synectiks.process.server.inputs.converters.UppercaseConverter;
import com.synectiks.process.server.plugin.inputs.Converter;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class UppercaseConverterTest {

    @Test
    public void testConvert() throws Exception {
        Converter c = new UppercaseConverter(new HashMap<String, Object>());

        assertNull(c.convert(null));
        assertEquals("", c.convert(""));
        assertEquals("FOOBAR", c.convert("foobar"));
        assertEquals("FOO BAR", c.convert("foo BAR"));
        assertEquals("FOOBAR", c.convert("FooBar"));
        assertEquals("FOOBAR ", c.convert("foobar "));
        assertEquals(" FOOBAR", c.convert(" foobar"));
        assertEquals("FOOBAR", c.convert("FOOBAR"));
    }

}
