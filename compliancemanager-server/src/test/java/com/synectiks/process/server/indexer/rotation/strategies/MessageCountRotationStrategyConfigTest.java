/*
 * */
package com.synectiks.process.server.indexer.rotation.strategies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.synectiks.process.server.indexer.rotation.strategies.MessageCountRotationStrategyConfig;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategyConfig;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageCountRotationStrategyConfigTest {
    @Test
    public void testCreate() throws Exception {
        final MessageCountRotationStrategyConfig config = MessageCountRotationStrategyConfig.create(1000);
        assertThat(config.maxDocsPerIndex()).isEqualTo(1000);
    }

    @Test
    public void testSerialization() throws JsonProcessingException {
        final RotationStrategyConfig config = MessageCountRotationStrategyConfig.create(1000);
        final ObjectMapper objectMapper = new ObjectMapperProvider().get();
        final String json = objectMapper.writeValueAsString(config);

        final Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        assertThat((String) JsonPath.read(document, "$.type")).isEqualTo("org.graylog2.indexer.rotation.strategies.MessageCountRotationStrategyConfig");
        assertThat((Integer) JsonPath.read(document, "$.max_docs_per_index")).isEqualTo(1000);
    }

    @Test
    public void testDeserialization() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapperProvider().get();
        final String json = "{ \"type\": \"org.graylog2.indexer.rotation.strategies.MessageCountRotationStrategyConfig\", \"max_docs_per_index\": 1000 }";
        final RotationStrategyConfig config = objectMapper.readValue(json, RotationStrategyConfig.class);

        assertThat(config).isInstanceOf(MessageCountRotationStrategyConfig.class);
        assertThat(((MessageCountRotationStrategyConfig) config).maxDocsPerIndex()).isEqualTo(1000);
    }
}