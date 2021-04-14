/*
 * */
package com.synectiks.process.server.indexer.rotation.strategies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.synectiks.process.server.indexer.rotation.strategies.SizeBasedRotationStrategyConfig;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategyConfig;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class SizeBasedRotationStrategyConfigTest {
    @Test
    public void testCreate() throws Exception {
        final SizeBasedRotationStrategyConfig config = SizeBasedRotationStrategyConfig.create(1000L);
        assertThat(config.maxSize()).isEqualTo(1000L);
    }

    @Test
    public void testSerialization() throws JsonProcessingException {
        final RotationStrategyConfig config = SizeBasedRotationStrategyConfig.create(1000L);
        final ObjectMapper objectMapper = new ObjectMapperProvider().get();
        final String json = objectMapper.writeValueAsString(config);

        final Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        assertThat((String) JsonPath.read(document, "$.type")).isEqualTo("org.graylog2.indexer.rotation.strategies.SizeBasedRotationStrategyConfig");
        assertThat((Integer) JsonPath.read(document, "$.max_size")).isEqualTo(1000);
    }

    @Test
    public void testDeserialization() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapperProvider().get();
        final String json = "{ \"type\": \"org.graylog2.indexer.rotation.strategies.SizeBasedRotationStrategyConfig\", \"max_size\": 1000 }";
        final RotationStrategyConfig config = objectMapper.readValue(json, RotationStrategyConfig.class);

        assertThat(config).isInstanceOf(SizeBasedRotationStrategyConfig.class);
        assertThat(((SizeBasedRotationStrategyConfig) config).maxSize()).isEqualTo(1000);
    }
}