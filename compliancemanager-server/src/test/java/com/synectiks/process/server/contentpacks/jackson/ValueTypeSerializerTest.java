/*
 * */
package com.synectiks.process.server.contentpacks.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueType;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValueTypeSerializerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void serialize() throws JsonProcessingException {
        assertThat(objectMapper.writeValueAsString(ValueType.BOOLEAN)).isEqualTo("\"boolean\"");
        assertThat(objectMapper.writeValueAsString(ValueType.DOUBLE)).isEqualTo("\"double\"");
        assertThat(objectMapper.writeValueAsString(ValueType.FLOAT)).isEqualTo("\"float\"");
        assertThat(objectMapper.writeValueAsString(ValueType.INTEGER)).isEqualTo("\"integer\"");
        assertThat(objectMapper.writeValueAsString(ValueType.LONG)).isEqualTo("\"long\"");
        assertThat(objectMapper.writeValueAsString(ValueType.STRING)).isEqualTo("\"string\"");
        assertThat(objectMapper.writeValueAsString(ValueType.PARAMETER)).isEqualTo("\"parameter\"");
    }
}