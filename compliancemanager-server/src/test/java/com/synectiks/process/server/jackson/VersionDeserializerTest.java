/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.zafarkhaja.semver.Version;
import com.synectiks.process.server.jackson.VersionDeserializer;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class VersionDeserializerTest {
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper()
                .registerModule(new SimpleModule().addDeserializer(Version.class, new VersionDeserializer()));
    }

    @Test
    public void successfullyDeserializesString() throws IOException {
        final Version version = objectMapper.readValue("\"1.3.7-rc.2+build.2.b8f12d7\"", Version.class);
        assertThat(version).isEqualTo(Version.valueOf("1.3.7-rc.2+build.2.b8f12d7"));
    }

    @Test
    public void successfullyDeserializesInteger() throws IOException {
        final Version version = objectMapper.readValue("5", Version.class);
        assertThat(version).isEqualTo(Version.forIntegers(5));
    }

    @Test
    public void successfullyDeserializesNull() throws IOException {
        final Version version = objectMapper.readValue("null", Version.class);
        assertThat(version).isNull();
    }

    @Test
    public void failsForInvalidType() throws IOException {
        try {
            objectMapper.readValue("[]", Version.class);
            fail("Expected JsonMappingException");
        } catch (JsonMappingException e) {
            assertThat(e).hasMessageStartingWith("Unexpected token (START_ARRAY), expected VALUE_STRING: expected String or Number");
        }
    }
}