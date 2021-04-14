/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.synectiks.process.server.jackson.SemverRequirementSerializer;
import com.synectiks.process.server.semver4j.Requirement;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SemverRequirementSerializerTest {
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper()
                .registerModule(new SimpleModule().addSerializer(new SemverRequirementSerializer()));
    }

    @Test
    public void successfullySerializesRequirement() throws JsonProcessingException {
        final Requirement requirement = Requirement.buildNPM("^1.3.7-rc.2+build.2.b8f12d7");
        final String s = objectMapper.writeValueAsString(requirement);
        assertThat(s).isEqualTo("\">=1.3.7-rc.2+build.2.b8f12d7 <2.0.0\"");
    }
}