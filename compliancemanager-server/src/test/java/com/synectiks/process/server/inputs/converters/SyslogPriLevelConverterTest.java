/*
 * */
package com.synectiks.process.server.inputs.converters;

import org.junit.Test;

import com.synectiks.process.server.inputs.converters.SyslogPriLevelConverter;
import com.synectiks.process.server.plugin.inputs.Converter;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class SyslogPriLevelConverterTest {

    @Test
    public void testConvert() throws Exception {
        Converter hc = new SyslogPriLevelConverter(new HashMap<String, Object>());
        assertNull(hc.convert(null));
        assertEquals("", hc.convert(""));
        assertEquals("lol no number", hc.convert("lol no number"));

        assertEquals(6, hc.convert("14")); // info
        assertEquals(4, hc.convert("12")); // warning
        assertEquals(7, hc.convert("7")); // debug
        assertEquals(7, hc.convert("87")); // debug
        assertEquals(5, hc.convert("5")); // notice
    }

}
