/*
 * */
package com.synectiks.process.common.plugins.views.search.searchtypes.eventlist;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.events.event.EventDto;
import com.synectiks.process.common.plugins.views.search.searchtypes.events.EventSummary;
import com.synectiks.process.server.plugin.Tools;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class EventSummaryTest {
    @Test
    public void testParseRawEvent() {
        final DateTime now = DateTime.now(DateTimeZone.UTC);
        final List<String> streams = new ArrayList<>();
        streams.add("stream-id-1");
        streams.add("stream-id-2");
        final Map<String, Object> rawEvent = ImmutableMap.of(
                EventDto.FIELD_ID, "dead-beef",
                EventDto.FIELD_MESSAGE, "message",
                EventDto.FIELD_SOURCE_STREAMS, streams,
                EventDto.FIELD_EVENT_TIMESTAMP, now.toString(Tools.ES_DATE_FORMAT_FORMATTER),
                EventDto.FIELD_ALERT, false
        );

        EventSummary eventSummary = EventSummary.parse(rawEvent);
        assertThat(eventSummary.id()).isEqualTo("dead-beef");
        assertThat(eventSummary.message()).isEqualTo("message");
        assertThat(eventSummary.streams()).isEqualTo(ImmutableSet.of("stream-id-1", "stream-id-2"));
        assertThat(eventSummary.timestamp().toString(Tools.ES_DATE_FORMAT_FORMATTER))
                .isEqualTo(now.toString(Tools.ES_DATE_FORMAT_FORMATTER));
        assertThat(eventSummary.alert()).isEqualTo(false);
    }
}
