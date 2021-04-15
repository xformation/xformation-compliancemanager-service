/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6.views.export;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.synectiks.process.common.plugins.views.search.elasticsearch.ElasticsearchQueryString;
import com.synectiks.process.common.plugins.views.search.elasticsearch.IndexLookup;
import com.synectiks.process.common.plugins.views.search.export.ExportException;
import com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand;
import com.synectiks.process.common.plugins.views.search.export.SimpleMessage;
import com.synectiks.process.common.plugins.views.search.export.SimpleMessageChunk;
import com.synectiks.process.common.plugins.views.search.export.TestData;
import com.synectiks.process.common.storage.elasticsearch6.views.export.ElasticsearchExportBackend;
import com.synectiks.process.common.storage.elasticsearch6.views.export.RequestStrategy;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchBaseTest;
import com.synectiks.process.server.indexer.ElasticsearchException;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.InvalidRangeParametersException;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public abstract class ElasticsearchExportBackendITBase extends ElasticsearchBaseTest {

    private IndexLookup indexLookup;
    private ElasticsearchExportBackend sut;

    protected abstract RequestStrategy requestStrategy();

    @Before
    public void setUp() {
        indexLookup = mock(IndexLookup.class);

        sut = new ElasticsearchExportBackend(indexLookup, requestStrategy(), false);
    }

    @Test
    public void usesCorrectIndicesAndStreams() {
        importFixture("messages.json");

        ExportMessagesCommand command = commandBuilderWithAllStreams()
                .streams(ImmutableSet.of("stream-01", "stream-02"))
                .build();

        mockIndexLookupFor(command, "graylog_0", "graylog_1");

        runWithExpectedResultIgnoringSort(command, "timestamp,source,message",
                "graylog_0, 2015-01-01T01:00:00.000Z, source-1, Ha",
                "graylog_1, 2015-01-01T02:00:00.000Z, source-2, He",
                "graylog_0, 2015-01-01T04:00:00.000Z, source-2, Ho"
        );
    }

    protected ExportMessagesCommand.Builder commandBuilderWithAllStreams() {
        return defaultCommandBuilder().streams(ImmutableSet.of("stream-01", "stream-02", "stream-03"));
    }

    @Test
    public void usesQueryString() {
        importFixture("messages.json");

        ExportMessagesCommand command = commandBuilderWithAllStreams()
                .queryString(ElasticsearchQueryString.builder().queryString("Ha Ho").build())
                .build();

        runWithExpectedResultIgnoringSort(command, "timestamp,source,message",
                "graylog_0, 2015-01-01T04:00:00.000Z, source-2, Ho",
                "graylog_0, 2015-01-01T01:00:00.000Z, source-1, Ha"
        );
    }

    @Test
    public void usesTimeRange() {
        importFixture("messages.json");

        ExportMessagesCommand command = commandBuilderWithAllStreams()
                .timeRange(timerange("2015-01-01T00:00:00.000Z", "2015-01-01T02:00:00.000Z"))
                .build();

        runWithExpectedResultIgnoringSort(command, "timestamp,source,message",
                "graylog_1, 2015-01-01T02:00:00.000Z, source-2, He",
                "graylog_0, 2015-01-01T01:00:00.000Z, source-1, Ha"
        );
    }

    @Test
    public void usesFieldsInOrder() {
        importFixture("messages.json");

        ExportMessagesCommand command = commandBuilderWithAllStreams()
                .fieldsInOrder("timestamp", "message")
                .build();

        runWithExpectedResultIgnoringSort(command, "timestamp,message",
                "graylog_0, 2015-01-01T04:00:00.000Z, Ho",
                "graylog_0, 2015-01-01T03:00:00.000Z, Hi",
                "graylog_1, 2015-01-01T02:00:00.000Z, He",
                "graylog_0, 2015-01-01T01:00:00.000Z, Ha");
    }

    @Test
    public void marksFirstChunk() {
        importFixture("messages.json");

        ExportMessagesCommand command = commandBuilderWithAllStreams().build();

        SimpleMessageChunk[] chunks = collectChunksFor(command).toArray(new SimpleMessageChunk[0]);

        assertThat(chunks[0].isFirstChunk()).isTrue();
    }

    @Test
    public void failsWithLeadingHighlightQueryIfDisallowed() {
        importFixture("messages.json");

        ExportMessagesCommand command = commandBuilderWithAllStreams().queryString(ElasticsearchQueryString.builder().queryString("*a").build()).build();

        assertThatExceptionOfType(ExportException.class)
                .isThrownBy(() -> sut.run(command, chunk -> {}))
                .withCauseInstanceOf(ElasticsearchException.class);
    }

    @Test
    public void respectsResultLimitIfSet() {
        importFixture("messages.json");

        ExportMessagesCommand command = commandBuilderWithAllStreams().chunkSize(1).limit(3).build();

        SimpleMessageChunk totalResult = collectTotalResult(command);

        assertThat(totalResult.messages()).hasSize(3);
    }

    @Test
    public void deliversCompleteLastChunkIfLimitIsReached() {
        importFixture("messages.json");

        ExportMessagesCommand command = commandBuilderWithAllStreams().chunkSize(2).limit(3).build();

        SimpleMessageChunk totalResult = collectTotalResult(command);

        assertThat(totalResult.messages()).hasSize(4);
    }

    @Test
    public void resultsHaveAllMessageFields() {
        importFixture("messages.json");

        ExportMessagesCommand command = commandBuilderWithAllStreams()
                .fieldsInOrder("timestamp", "message")
                .build();

        LinkedHashSet<SimpleMessageChunk> allChunks = collectChunksFor(command);
        SimpleMessageChunk totalResult = allChunks.iterator().next();

        Set<String> allFieldsInResult = actualFieldNamesFrom(totalResult);

        assertThat(allFieldsInResult).containsExactlyInAnyOrder(
                "xfcompliance_message_id",
                "source",
                "message",
                "timestamp",
                "streams",
                "_id");
    }

    private Set<String> actualFieldNamesFrom(SimpleMessageChunk chunk) {
        return chunk.messages()
                .stream().map(m -> m.fields().keySet()).reduce(Sets::union)
                .orElseThrow(() -> new RuntimeException("failed to collect field names"));
    }

    public void mockIndexLookupFor(ExportMessagesCommand command, String... indexNames) {
        when(indexLookup.indexNamesForStreamsInTimeRange(command.streams(), command.timeRange()))
                .thenReturn(ImmutableSet.copyOf(indexNames));
    }

    private ExportMessagesCommand.Builder defaultCommandBuilder() {
        return ExportMessagesCommand.withDefaults().toBuilder()
                .timeRange(allMessagesTimeRange());
    }

    protected void runWithExpectedResult(ExportMessagesCommand command, @SuppressWarnings("SameParameterValue") String resultFields, String... messageValues) {
        SimpleMessageChunk totalResult = collectTotalResult(command);

        assertResultMatches(resultFields, totalResult, messageValues, true);
    }

    protected void runWithExpectedResultIgnoringSort(ExportMessagesCommand command, String resultFields, String... messageValues) {
        SimpleMessageChunk totalResult = collectTotalResult(command);

        assertResultMatches(resultFields, totalResult, messageValues, false);
    }

    private void assertResultMatches(String resultFields, SimpleMessageChunk totalResult, String[] messageValues, boolean expectSorted) {
        Object[][] values = Arrays.stream(messageValues).map(this::toObjectArray).toArray(Object[][]::new);

        SimpleMessageChunk expected = TestData.simpleMessageChunkWithIndexNames(resultFields, values);

        assertThat(totalResult).isEqualTo(expected);

        if (expectSorted) {
            assertThat(totalResult.messages()).containsExactlyElementsOf(expected.messages());
        }
    }

    private Object[] toObjectArray(String s) {
        return Arrays.stream(s.split(",")).map(String::trim).toArray();
    }

    private SimpleMessageChunk collectTotalResult(ExportMessagesCommand command) {
        LinkedHashSet<SimpleMessageChunk> allChunks = collectChunksFor(command);

        LinkedHashSet<SimpleMessage> allMessages = new LinkedHashSet<>();

        for (SimpleMessageChunk chunk : allChunks) {
            keepOnlyRelevantFields(chunk, command.fieldsInOrder());
            allMessages.addAll(chunk.messages());
        }

        return SimpleMessageChunk.from(command.fieldsInOrder(), allMessages);
    }

    private void keepOnlyRelevantFields(SimpleMessageChunk chunk, LinkedHashSet<String> relevantFields) {
        for (SimpleMessage msg : chunk.messages()) {
            Set<String> allFieldsInMessage = ImmutableSet.copyOf(msg.fields().keySet());
            for (String name : allFieldsInMessage) {
                if (!relevantFields.contains(name)) {
                    msg.fields().remove(name);
                }
            }
        }
    }

    private LinkedHashSet<SimpleMessageChunk> collectChunksFor(ExportMessagesCommand command) {
        LinkedHashSet<SimpleMessageChunk> allChunks = new LinkedHashSet<>();

        sut.run(command, allChunks::add);
        return allChunks;
    }

    private AbsoluteRange allMessagesTimeRange() {
        return timerange("2015-01-01T00:00:00.000Z", "2015-01-03T00:00:00.000Z");
    }

    private AbsoluteRange timerange(@SuppressWarnings("SameParameterValue") String from, String to) {
        try {
            return AbsoluteRange.create(from, to);
        } catch (InvalidRangeParametersException e) {
            throw new RuntimeException(e);
        }
    }
}
