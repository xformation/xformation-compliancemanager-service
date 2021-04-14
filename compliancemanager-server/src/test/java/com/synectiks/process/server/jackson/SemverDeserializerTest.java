/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.synectiks.process.server.jackson.SemverDeserializer;
import com.synectiks.process.server.semver4j.Semver;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SemverDeserializerTest {
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper()
                .registerModule(new SimpleModule().addDeserializer(Semver.class, new SemverDeserializer()));
    }

    @Test
    public void successfullyDeserializesString() throws IOException {
        final Semver version = objectMapper.readValue("\"1.3.7-rc.2+build.2.b8f12d7\"", Semver.class);
        assertThat(version).isEqualTo(new Semver("1.3.7-rc.2+build.2.b8f12d7", Semver.SemverType.NPM));
    }

    @Test
    public void successfullyDeserializesInteger() throws IOException {
        final Semver version = objectMapper.readValue("5", Semver.class);
        assertThat(version).isEqualTo(new Semver("5", Semver.SemverType.LOOSE));
    }

    @Test
    public void successfullyDeserializesNull() throws IOException {
        final Semver version = objectMapper.readValue("null", Semver.class);
        assertThat(version).isNull();
    }

    @Test
    public void failsForInvalidVersion() {
        assertThatThrownBy(() -> objectMapper.readValue("\"foobar\"", Semver.class))
                .isInstanceOf(JsonMappingException.class)
                .hasMessageStartingWith("Invalid version (no major version): foobar");
    }

    @Test
    public void failsForInvalidType() {
        assertThatThrownBy(() -> objectMapper.readValue("[]", Semver.class))
                .isInstanceOf(JsonMappingException.class)
                .hasMessageStartingWith("Unexpected token (START_ARRAY), expected VALUE_STRING: expected String or Number");
    }
}