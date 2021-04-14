/*
 * */
package com.synectiks.process.server.contentpacks.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synectiks.process.server.contentpacks.model.ModelVersion;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ModelVersionTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void deserialize() {
        final ModelVersion modelVersion = ModelVersion.of("foobar");
        final JsonNode jsonNode = objectMapper.convertValue(modelVersion, JsonNode.class);

        assertThat(jsonNode.isTextual()).isTrue();
        assertThat(jsonNode.asText()).isEqualTo("foobar");
    }

    @Test
    public void serialize() throws IOException {
        final ModelVersion modelVersion = objectMapper.readValue("\"foobar\"", ModelVersion.class);
        assertThat(modelVersion).isEqualTo(ModelVersion.of("foobar"));
    }

    @Test
    public void ensureVersionIsNotBlank() {
        assertThatThrownBy(() -> ModelVersion.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Version must not be blank");
        assertThatThrownBy(() -> ModelVersion.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Version must not be blank");
        assertThatThrownBy(() -> ModelVersion.of("    \n\r\t"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Version must not be blank");
    }
}