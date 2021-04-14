/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.synectiks.process.server.jackson.MongoZonedDateTimeDeserializer;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class MongoZonedDateTimeDeserializerTest {
    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Test
    public void deserializeZonedDateTime() throws Exception {
        final String json = "{\"date_time\":\"2016-12-13T16:00:00.000+0200\"}";
        final TestBean value = objectMapper.readValue(json, TestBean.class);
        assertThat(value.dateTime).isEqualTo(ZonedDateTime.of(2016, 12, 13, 14, 0, 0, 0, ZoneOffset.UTC));
    }

    @Test
    public void deserializeNull() throws Exception {
        final String json = "{\"date_time\":null}";
        final TestBean value = objectMapper.readValue(json, TestBean.class);
        assertThat(value.dateTime).isNull();
    }

    static class TestBean {
        @JsonDeserialize(using = MongoZonedDateTimeDeserializer.class)
        ZonedDateTime dateTime;
    }
}