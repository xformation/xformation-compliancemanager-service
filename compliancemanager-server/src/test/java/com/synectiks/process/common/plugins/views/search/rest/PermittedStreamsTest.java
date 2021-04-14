/*
 * */
package com.synectiks.process.common.plugins.views.search.rest;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.plugins.views.search.rest.PermittedStreams;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.streams.StreamService;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.synectiks.process.server.plugin.streams.Stream.DEFAULT_EVENT_STREAM_IDS;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PermittedStreamsTest {

    private StreamService streamService;
    private PermittedStreams sut;

    @Before
    public void setUp() throws Exception {
        GuiceInjectorHolder.createInjector(Collections.emptyList());
        streamService = mock(StreamService.class);
        sut = new PermittedStreams(streamService);
    }

    @Test
    public void findsStreams() {
        stubStreams("oans", "zwoa", "gsuffa");

        ImmutableSet<String> result = sut.load(id -> true);

        assertThat(result).containsExactlyInAnyOrder("oans", "zwoa", "gsuffa");
    }

    @Test
    public void filtersOutNonPermittedStreams() {
        stubStreams("oans", "zwoa", "gsuffa");

        ImmutableSet<String> result = sut.load(id -> id.equals("gsuffa"));

        assertThat(result).containsExactly("gsuffa");
    }

    @Test
    public void returnsEmptyListIfNoStreamsFound() {
        stubStreams("oans", "zwoa", "gsuffa");

        ImmutableSet<String> result = sut.load(id -> false);

        assertThat(result).isEmpty();
    }

    @Test
    public void filtersDefaultStreams() {
        List<String> streamIds = new ArrayList<>(DEFAULT_EVENT_STREAM_IDS);
        streamIds.add("i'm ok");

        stubStreams(streamIds.toArray(new String[]{}));

        ImmutableSet<String> result = sut.load(id -> true);

        assertThat(result).containsExactly("i'm ok");
    }

    private void stubStreams(String... streamIds) {
        List<Stream> streams = streamsWithIds(streamIds);
        when(streamService.loadAll()).thenReturn(streams);
    }

    private List<Stream> streamsWithIds(String... ids) {
        return Arrays.stream(ids).map(this::streamWithId).collect(toList());
    }

    private Stream streamWithId(String id) {
        Stream s = mock(Stream.class);
        when(s.getId()).thenReturn(id);
        return s;
    }
}
