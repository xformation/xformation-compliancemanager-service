/*
 * */
package com.synectiks.process.common.plugins.views.search.searchtypes.eventlist;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.plugins.views.search.searchtypes.events.EventList;

import org.junit.Test;

import static com.synectiks.process.server.plugin.streams.Stream.DEFAULT_EVENTS_STREAM_ID;
import static com.synectiks.process.server.plugin.streams.Stream.DEFAULT_SYSTEM_EVENTS_STREAM_ID;
import static org.assertj.core.api.Assertions.assertThat;

public class EventListTest {

    @Test
    public void testEffectiveStream() {
        final EventList eventList = EventList.builder()
                .streams(ImmutableSet.of("dead-beef", "1337-beef"))
                .build();
        assertThat(eventList.effectiveStreams()).isEqualTo(
                ImmutableSet.of(DEFAULT_EVENTS_STREAM_ID, DEFAULT_SYSTEM_EVENTS_STREAM_ID)
        );
        assertThat(eventList.streams()).isEqualTo(ImmutableSet.of("dead-beef", "1337-beef"));
    }
}
