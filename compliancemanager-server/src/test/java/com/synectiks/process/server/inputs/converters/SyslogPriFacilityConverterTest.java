/*
 * */
package com.synectiks.process.server.inputs.converters;

import org.junit.Test;

import com.synectiks.process.server.inputs.converters.SyslogPriFacilityConverter;
import com.synectiks.process.server.plugin.inputs.Converter;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class SyslogPriFacilityConverterTest {

    @Test
    public void testConvert() throws Exception {
        Converter hc = new SyslogPriFacilityConverter(new HashMap<String, Object>());
        assertNull(hc.convert(null));
        assertEquals("", hc.convert(""));
        assertEquals("lol no number", hc.convert("lol no number"));

        assertEquals("user-level", hc.convert("14")); // user-level
        assertEquals("kernel", hc.convert("5")); // kernel
        assertEquals("security/authorization", hc.convert("87")); // security/authorization
    }

}
