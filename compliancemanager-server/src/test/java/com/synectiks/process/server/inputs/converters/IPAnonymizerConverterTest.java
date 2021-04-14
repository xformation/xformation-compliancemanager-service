/*
 * */
package com.synectiks.process.server.inputs.converters;

import org.junit.Test;

import com.synectiks.process.server.inputs.converters.IPAnonymizerConverter;
import com.synectiks.process.server.plugin.inputs.Converter;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class IPAnonymizerConverterTest {

    @Test
    public void testConvert() throws Exception {
        Converter hc = new IPAnonymizerConverter(new HashMap<String, Object>());

        assertNull(hc.convert(null));
        assertEquals("", hc.convert(""));
        assertEquals("lol no IP in here", hc.convert("lol no IP in here"));
        assertEquals("127.0.1", hc.convert("127.0.1"));
        assertEquals("127.0.0.xxx", hc.convert("127.0.0.xxx"));

        assertEquals("127.0.0.xxx", hc.convert("127.0.0.1"));
        assertEquals("127.0.0.xxx foobar 192.168.1.xxx test", hc.convert("127.0.0.1 foobar 192.168.1.100 test"));
    }

}
