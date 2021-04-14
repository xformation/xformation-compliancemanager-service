/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.SearchType;
import com.synectiks.process.common.plugins.views.search.engine.BackendQuery;
import com.synectiks.process.common.plugins.views.search.export.SimpleMessage;
import com.synectiks.process.common.plugins.views.search.export.SimpleMessageChunk;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.InvalidRangeParametersException;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.RelativeRange;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand.defaultTimeRange;
import static java.util.stream.Collectors.toCollection;

public class TestData {

    public static Query.Builder validQueryBuilderWith(SearchType searchType) {
        return validQueryBuilder().searchTypes(ImmutableSet.of(searchType));
    }

    public static Query.Builder validQueryBuilder() {
        return Query.builder().id(UUID.randomUUID().toString())
                .timerange(defaultTimeRange())
                .query(new BackendQuery.Fallback());
    }

    public static SimpleMessageChunk simpleMessageChunk(String fieldNames, Object[]... messageValues) {
        LinkedHashSet<SimpleMessage> messages = Arrays.stream(messageValues)
                .map(s -> simpleMessage(fieldNames, s))
                .collect(toCollection(LinkedHashSet::new));
        return SimpleMessageChunk.from(setFrom(fieldNames), messages);
    }

    public static SimpleMessageChunk simpleMessageChunkWithIndexNames(String fieldNames, Object[]... messageValues) {
        LinkedHashSet<SimpleMessage> messages = Arrays.stream(messageValues)
                .map(values -> simpleMessageWithIndexName(fieldNames, values))
                .collect(toCollection(LinkedHashSet::new));
        return SimpleMessageChunk.from(setFrom(fieldNames), messages);
    }

    private static SimpleMessage simpleMessageWithIndexName(String fieldNames, Object[] values) {
        String indexName = (String) values[0];
        Object[] fieldValues = Arrays.copyOfRange(values, 1, values.length);
        return simpleMessage(indexName, fieldNames, fieldValues);
    }

    private static LinkedHashSet<String> setFrom(String fieldNames) {
        return Arrays.stream(fieldNames.split(","))
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static SimpleMessage simpleMessage(String indexName, String fieldNames, Object[] values) {
        LinkedHashSet<String> names = setFrom(fieldNames);
        LinkedHashMap<String, Object> fields = new LinkedHashMap<>();
        int i = 0;
        for (String name : names) {
            fields.put(name, values[i++]);
        }
        return SimpleMessage.from(indexName, fields);
    }

    public static SimpleMessage simpleMessage(String fieldNames, Object[] values) {
        return simpleMessage("some-index", fieldNames, values);
    }

    public static RelativeRange relativeRange(int range) {
        try {
            return RelativeRange.create(range);
        } catch (InvalidRangeParametersException e) {
            throw new RuntimeException(e);
        }
    }
}
