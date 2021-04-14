/*
 * */
package com.synectiks.process.server.indexer.retention.strategies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.synectiks.process.server.indexer.retention.strategies.DeletionRetentionStrategyConfig;
import com.synectiks.process.server.plugin.indexer.retention.RetentionStrategyConfig;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class DeletionRetentionStrategyConfigTest {
    @Test
    public void testCreate() throws Exception {
        final DeletionRetentionStrategyConfig config = DeletionRetentionStrategyConfig.create(21);

        assertThat(config.maxNumberOfIndices()).isEqualTo(21);
    }

    @Test
    public void testSerialization() throws JsonProcessingException {
        final DeletionRetentionStrategyConfig config = DeletionRetentionStrategyConfig.create(25);
        final ObjectMapper objectMapper = new ObjectMapperProvider().get();
        final String json = objectMapper.writeValueAsString(config);

        final Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        assertThat((String) JsonPath.read(document, "$.type")).isEqualTo("org.graylog2.indexer.retention.strategies.DeletionRetentionStrategyConfig");
        assertThat((Integer) JsonPath.read(document, "$.max_number_of_indices")).isEqualTo(25);
    }

    @Test
    public void testDeserialization() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapperProvider().get();
        final String json = "{ \"type\": \"org.graylog2.indexer.retention.strategies.DeletionRetentionStrategyConfig\", \"max_number_of_indices\": 23}";
        final RetentionStrategyConfig config = objectMapper.readValue(json, RetentionStrategyConfig.class);

        assertThat(config).isInstanceOf(DeletionRetentionStrategyConfig.class);
        assertThat(((DeletionRetentionStrategyConfig) config).maxNumberOfIndices()).isEqualTo(23);
    }
}