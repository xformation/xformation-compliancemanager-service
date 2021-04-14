/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.synectiks.process.server.jackson.SemverRequirementDeserializer;
import com.synectiks.process.server.semver4j.Requirement;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SemverRequirementDeserializerTest {
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper()
                .registerModule(new SimpleModule().addDeserializer(Requirement.class, new SemverRequirementDeserializer()));
    }

    @Test
    public void successfullyDeserializesString() throws IOException {
        final Requirement requirement = objectMapper.readValue("\"^1.3.7-rc.2+build.2.b8f12d7\"", Requirement.class);
        assertThat(requirement).isEqualTo(Requirement.buildNPM("^1.3.7-rc.2+build.2.b8f12d7"));
    }

    @Test
    public void successfullyDeserializesNull() throws IOException {
        final Requirement requirement = objectMapper.readValue("null", Requirement.class);
        assertThat(requirement).isNull();
    }

    @Test
    public void failsForInvalidRequirementExpression() {
        assertThatThrownBy(() -> objectMapper.readValue("\"foobar\"", Requirement.class))
                .isInstanceOf(JsonMappingException.class)
                .hasMessageStartingWith("Invalid version (no major version): foobar");
    }

    @Test
    public void failsForInvalidType() {
        assertThatThrownBy(() -> objectMapper.readValue("[]", Requirement.class))
                .isInstanceOf(JsonMappingException.class)
                .hasMessageStartingWith("Unexpected token (START_ARRAY), expected VALUE_STRING");
    }
}