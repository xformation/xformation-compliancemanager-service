/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.synectiks.process.server.jackson.MongoZonedDateTimeSerializer;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class MongoZonedDateTimeSerializerTest {
    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Test
    public void serializeZonedDateTime() throws Exception {
        final TestBean testBean = new TestBean(ZonedDateTime.of(2016, 12, 13, 16, 0, 0, 0, ZoneOffset.ofHours(2)));
        final String valueAsString = objectMapper.writeValueAsString(testBean);
        assertThat(valueAsString)
                .isNotNull()
                .isEqualTo("{\"date_time\":\"2016-12-13T14:00:00.000+0000\"}");
    }

    @Test
    public void serializeNull() throws Exception {
        final TestBean testBean = new TestBean(null);
        final String valueAsString = objectMapper.writeValueAsString(testBean);
        assertThat(valueAsString)
                .isNotNull()
                .isEqualTo("{\"date_time\":null}");
    }

    static class TestBean {
        @JsonSerialize(using = MongoZonedDateTimeSerializer.class)
        ZonedDateTime dateTime;

        public TestBean(ZonedDateTime dateTime) {
            this.dateTime = dateTime;
        }
    }
}