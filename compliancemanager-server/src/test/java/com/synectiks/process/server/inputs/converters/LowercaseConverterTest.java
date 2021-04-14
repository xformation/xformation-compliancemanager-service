/*
 * */
package com.synectiks.process.server.inputs.converters;

import org.junit.Test;

import com.synectiks.process.server.inputs.converters.LowercaseConverter;
import com.synectiks.process.server.plugin.inputs.Converter;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class LowercaseConverterTest {

    @Test
    public void testConvert() throws Exception {
        Converter c = new LowercaseConverter(new HashMap<String, Object>());

        assertNull(c.convert(null));
        assertEquals("", c.convert(""));
        assertEquals("foobar", c.convert("foobar"));
        assertEquals("foo bar", c.convert("foo BAR"));
        assertEquals("foobar", c.convert("FooBar"));
        assertEquals("foobar ", c.convert("foobar "));
        assertEquals(" foobar", c.convert(" foobar"));
        assertEquals("foobar", c.convert("FOOBAR"));
    }

}
