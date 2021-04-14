/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6.views.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.jayway.jsonpath.JsonPath;
import com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand;
import com.synectiks.process.common.storage.elasticsearch6.views.export.JestWrapper;
import com.synectiks.process.common.storage.elasticsearch6.views.export.SearchAfter;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import net.minidev.json.JSONArray;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.synectiks.process.common.storage.elasticsearch6.views.export.SearchAfter.DEFAULT_TIEBREAKER_FIELD;
import static com.synectiks.process.common.storage.elasticsearch6.views.export.SearchAfter.EVENTS_TIEBREAKER_FIELD;
import static com.synectiks.process.server.plugin.Message.FIELD_TIMESTAMP;
import static com.synectiks.process.server.plugin.streams.Stream.DEFAULT_EVENT_STREAM_IDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SearchAfterTest {
    private SearchAfter sut;
    private JestWrapper jestWrapper;
    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @BeforeEach
    void setUp() {
        jestWrapper = mock(JestWrapper.class);
        SearchResult emptyResult = new SearchResult(new ObjectMapper());

        doReturn(emptyResult).when(jestWrapper).execute(any(), any());
        sut = new SearchAfter(jestWrapper);
    }

    @Test
    void ignoresEventsStreamsForAMixOfEventStreamsAndOthers() {
        ImmutableSet<String> eventStreams = ImmutableSet.copyOf(DEFAULT_EVENT_STREAM_IDS);
        ImmutableSet<String> allowedStream = ImmutableSet.of("allowed-stream");
        Set<String> requestedStreams = Sets.union(eventStreams, allowedStream);

        Set<String> supportedStreams = sut.removeUnsupportedStreams(requestedStreams);

        assertThat(supportedStreams).containsOnly("allowed-stream");
    }

    @Test
    void supportsEventsStreamsOnly() {
        ImmutableSet<String> eventStreams = ImmutableSet.copyOf(DEFAULT_EVENT_STREAM_IDS);

        Set<String> supportedStreams = sut.removeUnsupportedStreams(eventStreams);

        assertThat(supportedStreams).containsOnlyElementsOf(DEFAULT_EVENT_STREAM_IDS);
    }

    @Test
    void usesIDAsTieBreakerForEventStreams() {
        ExportMessagesCommand command = ExportMessagesCommand.withDefaults().toBuilder()
                .streams(DEFAULT_EVENT_STREAM_IDS.asList().get(0)).build();

        sut.nextChunk(new Search.Builder(""), command);

        List<String> sortKeys = captureSortKeys();

        assertThat(sortKeys).containsExactly(FIELD_TIMESTAMP, EVENTS_TIEBREAKER_FIELD);
    }

    @Test
    void usesDefaultTieBreakerForNonEventStreams() {
        ExportMessagesCommand command = ExportMessagesCommand.withDefaults().toBuilder()
                .streams("stream-1", "stream-2").build();

        sut.nextChunk(new Search.Builder(""), command);

        List<String> sortKeys = captureSortKeys();

        assertThat(sortKeys).containsExactly(FIELD_TIMESTAMP, DEFAULT_TIEBREAKER_FIELD);
    }

    private List<String> captureSortKeys() {
        //noinspection unchecked
        ArgumentCaptor<Action<? extends JestResult>> captor = ArgumentCaptor.forClass(Action.class);

        verify(jestWrapper).execute(captor.capture(), any());

        return sortKeysFrom(captor.getValue());
    }

    private List<String> sortKeysFrom(Action<? extends JestResult> searchAction) {
        String rawJson = dataFrom(searchAction);
        JSONArray sorts = JsonPath.parse(rawJson).read("$.sort.*");
        return sorts.stream().map(this::singleKeyFrom).collect(Collectors.toList());
    }

    private String singleKeyFrom(Object jsonMap) {
        //noinspection unchecked
        return ((Map<String, Object>) jsonMap).keySet().iterator().next();
    }

    private String dataFrom(Action<? extends JestResult> searchAction) {
        try {
            return searchAction.getData(objectMapper);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
