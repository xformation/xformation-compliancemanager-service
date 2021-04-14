/*
 * */
package com.synectiks.process.server.plugin.lookup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synectiks.process.server.plugin.lookup.LookupCacheKey;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class LookupCacheKeyTest {
    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Test
    public void serialize() {
        final LookupCacheKey cacheKey = LookupCacheKey.createFromJSON("prefix", "key");
        final JsonNode node = objectMapper.convertValue(cacheKey, JsonNode.class);
        assertThat(node.isObject()).isTrue();
        assertThat(node.fieldNames()).containsExactlyInAnyOrder("prefix", "key");
        assertThat(node.path("prefix").isTextual()).isTrue();
        assertThat(node.path("prefix").asText()).isEqualTo("prefix");
        assertThat(node.path("key").isTextual()).isTrue();
        assertThat(node.path("key").asText()).isEqualTo("key");
    }

    @Test
    public void serializePrefixOnly() {
        final LookupCacheKey cacheKey = LookupCacheKey.createFromJSON("prefix", null);
        final JsonNode node = objectMapper.convertValue(cacheKey, JsonNode.class);
        assertThat(node.isObject()).isTrue();
        assertThat(node.fieldNames()).containsExactlyInAnyOrder("prefix", "key");
        assertThat(node.path("prefix").isTextual()).isTrue();
        assertThat(node.path("prefix").asText()).isEqualTo("prefix");
        assertThat(node.path("key").isNull()).isTrue();
    }

    @Test
    public void deserialize() throws IOException {
        final String json = "{\"prefix\":\"prefix\", \"key\":\"key\"}";
        final LookupCacheKey cacheKey = objectMapper.readValue(json, LookupCacheKey.class);
        assertThat(cacheKey.prefix()).isEqualTo("prefix");
        assertThat(cacheKey.key()).isEqualTo("key");
        assertThat(cacheKey.isPrefixOnly()).isFalse();
    }

    @Test
    public void deserializePrefixOnly() throws IOException {
        final String json = "{\"prefix\":\"prefix\"}";
        final LookupCacheKey cacheKey = objectMapper.readValue(json, LookupCacheKey.class);
        assertThat(cacheKey.prefix()).isEqualTo("prefix");
        assertThat(cacheKey.key()).isNull();
        assertThat(cacheKey.isPrefixOnly()).isTrue();
    }

}
