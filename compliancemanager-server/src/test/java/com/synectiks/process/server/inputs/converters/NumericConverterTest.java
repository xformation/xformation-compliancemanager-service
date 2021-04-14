/*
 * */
package com.synectiks.process.server.inputs.converters;

import org.junit.Test;

import com.synectiks.process.server.inputs.converters.NumericConverter;
import com.synectiks.process.server.plugin.inputs.Converter;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class NumericConverterTest {

    @Test
    public void testConvert() throws Exception {
        Converter hc = new NumericConverter(new HashMap<String, Object>());

        assertNull(hc.convert(null));
        assertEquals("", hc.convert(""));
        assertEquals("lol no number", hc.convert("lol no number"));
        assertEquals(9001, hc.convert("9001"));
        assertEquals(2147483648L, hc.convert("2147483648"));
        assertEquals(10.4D, hc.convert("10.4"));
        assertEquals(Integer.class, hc.convert("4").getClass());
    }

}
