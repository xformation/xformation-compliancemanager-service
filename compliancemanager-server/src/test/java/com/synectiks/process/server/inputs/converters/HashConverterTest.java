/*
 * */
package com.synectiks.process.server.inputs.converters;

import org.junit.Test;

import com.synectiks.process.server.inputs.converters.HashConverter;
import com.synectiks.process.server.plugin.inputs.Converter;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class HashConverterTest {

    @Test
    public void testConvert() throws Exception {
        Converter hc = new HashConverter(new HashMap<String, Object>());

        assertNull(hc.convert(null));
        assertEquals("", hc.convert(""));
        assertEquals("c029b5a72ae255853d7151a9e28c6260", hc.convert("graylog2"));
    }

}
