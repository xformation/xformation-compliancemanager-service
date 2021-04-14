/*
 * */
package com.synectiks.process.server.contentpacks.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synectiks.process.server.contentpacks.model.ModelId;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ModelIdTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void deserialize() {
        final ModelId modelId = ModelId.of("foobar");
        final JsonNode jsonNode = objectMapper.convertValue(modelId, JsonNode.class);

        assertThat(jsonNode.isTextual()).isTrue();
        assertThat(jsonNode.asText()).isEqualTo("foobar");
    }

    @Test
    public void serialize() throws IOException {
        final ModelId modelId = objectMapper.readValue("\"foobar\"", ModelId.class);
        assertThat(modelId).isEqualTo(ModelId.of("foobar"));
    }

    @Test
    public void ensureIdIsNotBlank() {
        assertThatThrownBy(() -> ModelId.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID must not be blank");
        assertThatThrownBy(() -> ModelId.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID must not be blank");
        assertThatThrownBy(() -> ModelId.of("    \n\r\t"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID must not be blank");
    }
}