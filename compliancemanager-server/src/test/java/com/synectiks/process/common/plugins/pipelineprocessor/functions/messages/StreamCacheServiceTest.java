/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.messages;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.messages.StreamCacheService;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.shared.SuppressForbidden;
import com.synectiks.process.server.streams.StreamImpl;
import com.synectiks.process.server.streams.StreamService;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;

import static com.synectiks.process.server.streams.StreamImpl.FIELD_INDEX_SET_ID;
import static com.synectiks.process.server.streams.StreamImpl.FIELD_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("UnstableApiUsage")
public class StreamCacheServiceTest {

    private StreamCacheService cacheService;

    private StreamService streamService;

    @Before
    @SuppressForbidden("Allow using default thread factory")
    public void setUp() throws Exception {
        streamService = mock(StreamService.class);
        cacheService = new StreamCacheService(new EventBus(), streamService,
                                              Executors.newSingleThreadScheduledExecutor());
    }

    @Test
    public void getByName() {
        // make sure getByName always returns a collection
        final Collection<Stream> streams = cacheService.getByName("nonexisting");
        assertThat(streams).isNotNull().isEmpty();
    }

    @Test
    public void multipleStreamsBySameName() {
        Stream stream1 = createStream(new ObjectId(), ImmutableMap.of(FIELD_TITLE, "title"));
        Stream stream2 = createStream(new ObjectId(), ImmutableMap.of(FIELD_TITLE, "title"));
        Stream stream3 = createStream(new ObjectId(), ImmutableMap.of(FIELD_TITLE, "different title"));

        when(streamService.loadAllEnabled()).thenReturn(ImmutableList.of(stream1, stream2, stream3));

        cacheService.updateStreams();

        assertEquals(ImmutableSet.of(stream1, stream2), cacheService.getByName("title"));
        assertEquals(ImmutableSet.of(stream3), cacheService.getByName("different title"));
    }

    @Test
    public void updatesStreamByName() {
        ObjectId streamId = new ObjectId();
        Stream stream = createStream(streamId, ImmutableMap.of(FIELD_TITLE, "title"));

        Stream modifiedStream = createStream(streamId, ImmutableMap.of(
                FIELD_TITLE, "title", FIELD_INDEX_SET_ID, "index-set-id"));

        when(streamService.loadAllEnabled()).thenReturn(ImmutableList.of(stream));
        cacheService.updateStreams();

        Collection<Stream> streams = cacheService.getByName("title");

        // using assertEquals instead of assertThat(streams).containsExactlyInAnyOrder() here to avoid using the
        // TreeSet comparator for containment checks
        assertEquals(Collections.singleton(stream), streams);

        when(streamService.loadAllEnabled()).thenReturn(ImmutableList.of(modifiedStream));
        cacheService.updateStreams();

        streams = cacheService.getByName("title");

        assertEquals(Collections.singleton(modifiedStream), streams);
    }

    @Test
    public void purgesStreamByName() {
        ObjectId streamId = new ObjectId();
        Stream stream = createStream(streamId, ImmutableMap.of(FIELD_TITLE, "title"));

        when(streamService.loadAllEnabled()).thenReturn(ImmutableList.of(stream));
        cacheService.updateStreams();

        assertThat(cacheService.getByName("title")).isNotEmpty();

        when(streamService.loadAllEnabled()).thenReturn(Collections.emptyList());
        cacheService.updateStreams();

        assertThat(cacheService.getByName("title")).isEmpty();
    }

    @Test
    public void titleChanges() {
        ObjectId streamId = new ObjectId();
        Stream stream = createStream(streamId, ImmutableMap.of(FIELD_TITLE, "title"));
        Stream modifiedStream = createStream(streamId, ImmutableMap.of(FIELD_TITLE, "new title"));

        when(streamService.loadAllEnabled()).thenReturn(ImmutableList.of(stream));
        cacheService.updateStreams();

        when(streamService.loadAllEnabled()).thenReturn(ImmutableList.of(modifiedStream));
        cacheService.updateStreams();

        Collection<Stream> streams = cacheService.getByName("title");
        assertThat(streams).isEmpty();

        streams = cacheService.getByName("new title");
        assertEquals(Collections.singleton(modifiedStream), streams);
    }

    private Stream createStream(ObjectId id, Map<String, Object> fields) {
        return new StreamImpl(id, fields, Collections.emptyList(), Collections.emptySet(), null);
    }
}
