/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.synectiks.process.server.jackson.SemverSerializer;
import com.synectiks.process.server.semver4j.Semver;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SemverSerializerTest {
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper()
                .registerModule(new SimpleModule().addSerializer(new SemverSerializer()));
    }

    @Test
    public void successfullySerializesVersion() throws JsonProcessingException {
        final Semver version = new Semver("1.3.7-rc.2+build.2.b8f12d7");
        final String s = objectMapper.writeValueAsString(version);
        assertThat(s).isEqualTo("\"1.3.7-rc.2+build.2.b8f12d7\"");
    }
}