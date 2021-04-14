/*
 * */
package com.synectiks.process.common.events.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.synectiks.process.common.events.event.EventDto;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.joda.time.DateTime;
import org.junit.Test;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class EventDtoTest {
    @Test
    public void ignoreIdFieldWithUnderscore() throws Exception {
        final URL eventString = Resources.getResource(getClass(), "filter-event-from-elasticsearch.json");
        final ObjectMapper objectMapper = new ObjectMapperProvider().get();

        final EventDto eventDto = objectMapper.readValue(eventString, EventDto.class);

        assertThat(eventDto.id()).isEqualTo("01DNM0DVJDV52NA5VEBTYJ6PJY");
    }

    @Test
    public void deserializeWithESTimestamps() throws Exception {
        // Checks that the EventDto is using the "ESMongoDateTimeDeserializer" deserializer to be able
        // to parse our ES timestamps.

        final URL eventString = Resources.getResource(getClass(), "aggregation-event-from-elasticsearch.json");
        final ObjectMapper objectMapper = new ObjectMapperProvider().get();

        final EventDto eventDto = objectMapper.readValue(eventString, EventDto.class);

        assertThat(eventDto.eventTimestamp()).isEqualTo(DateTime.parse("2019-08-21T07:48:01.326Z"));
        assertThat(eventDto.processingTimestamp()).isEqualTo(DateTime.parse("2019-09-25T10:35:57.116Z"));
        assertThat(eventDto.timerangeStart()).get().isEqualTo(DateTime.parse("2019-08-21T07:47:41.213Z"));
        assertThat(eventDto.timerangeEnd()).get().isEqualTo(DateTime.parse("2019-08-21T07:48:41.212Z"));
    }
}
