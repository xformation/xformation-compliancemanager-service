/*
 * */
package com.synectiks.process.server.indexer.rotation.strategies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.synectiks.process.server.indexer.rotation.strategies.TimeBasedRotationStrategyConfig;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategyConfig;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.joda.time.Period;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeBasedRotationStrategyConfigTest {
    @Test
    public void testCreate() throws Exception {
        final TimeBasedRotationStrategyConfig config = TimeBasedRotationStrategyConfig.create(Period.days(1));
        assertThat(config.rotationPeriod()).isEqualTo(Period.days(1));
    }

    @Test
    public void testSerialization() throws JsonProcessingException {
        final RotationStrategyConfig config = TimeBasedRotationStrategyConfig.create(Period.days(1));
        final ObjectMapper objectMapper = new ObjectMapperProvider().get();
        final String json = objectMapper.writeValueAsString(config);

        final Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        assertThat((String) JsonPath.read(document, "$.type")).isEqualTo("org.graylog2.indexer.rotation.strategies.TimeBasedRotationStrategyConfig");
        assertThat((String) JsonPath.read(document, "$.rotation_period")).isEqualTo("P1D");
    }

    @Test
    public void testDeserialization() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapperProvider().get();
        final String json = "{ \"type\": \"org.graylog2.indexer.rotation.strategies.TimeBasedRotationStrategyConfig\", \"rotation_period\": \"P1D\" }";
        final RotationStrategyConfig config = objectMapper.readValue(json, RotationStrategyConfig.class);

        assertThat(config).isInstanceOf(TimeBasedRotationStrategyConfig.class);
        assertThat(((TimeBasedRotationStrategyConfig) config).rotationPeriod()).isEqualTo(Period.days(1));
    }
}