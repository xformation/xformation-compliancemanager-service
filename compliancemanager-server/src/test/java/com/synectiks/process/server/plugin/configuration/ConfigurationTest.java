/*
 * */
package com.synectiks.process.server.plugin.configuration;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.plugin.configuration.Configuration;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ConfigurationTest {

    @Test
    public void testConfigSerialization() throws Exception {
        final ImmutableMap<String, Object> emptyMap = ImmutableMap.of();
        final Configuration emptyConfig = new Configuration(emptyMap);

        assertNull(emptyConfig.serializeToJson());
        assertNull(new Configuration(null).serializeToJson());

        final Map<String, Object> map = new HashMap<>();

        // Test might be broken depending on the iteration order...
        map.put("b", 1);
        map.put("a", 1);

        final String json = new Configuration(map).serializeToJson();
        assertEquals("{\"source\":{\"a\":1,\"b\":1}}", json);

        final Configuration config = Configuration.deserializeFromJson(json);

        assertTrue(config.intIsSet("a"));
        assertTrue(config.intIsSet("b"));

        final Configuration emptyConfigFromNull = Configuration.deserializeFromJson(null);

        assertNotNull(emptyConfigFromNull);
    }
}
