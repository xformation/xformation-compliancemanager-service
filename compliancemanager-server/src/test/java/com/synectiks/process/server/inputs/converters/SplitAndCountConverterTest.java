/*
 * */
package com.synectiks.process.server.inputs.converters;

import org.junit.Test;

import com.synectiks.process.server.ConfigurationException;
import com.synectiks.process.server.inputs.converters.SplitAndCountConverter;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SplitAndCountConverterTest {
    @Test
    public void testConvert() throws Exception {
        assertEquals(0, new SplitAndCountConverter(config("x")).convert(""));
        assertEquals(1, new SplitAndCountConverter(config("_")).convert("foo-bar-baz"));
        assertEquals(1, new SplitAndCountConverter(config("-")).convert("foo"));
        assertEquals(2, new SplitAndCountConverter(config("-")).convert("foo-bar"));
        assertEquals(3, new SplitAndCountConverter(config("-")).convert("foo-bar-baz"));
        assertEquals(3, new SplitAndCountConverter(config(".")).convert("foo.bar.baz")); // Regex. Must be escaped.
    }

    @Test(expected = ConfigurationException.class)
    public void testWithEmptyConfig() throws Exception {
        assertEquals(null, new SplitAndCountConverter(config("")).convert("foo"));
    }

    @Test(expected = ConfigurationException.class)
    public void testWithNullConfig() throws Exception {
        assertEquals(null, new SplitAndCountConverter(config(null)).convert("foo"));
    }

    private Map<String, Object> config(final String splitBy) {
        return Collections.singletonMap("split_by", splitBy);
    }
}
