/*
 * */
package com.synectiks.process.server.indexer.ranges;

import org.joda.time.DateTime;

import java.util.Comparator;
import java.util.List;

public interface IndexRange {
    String FIELD_TOOK_MS = "took_ms";
    String FIELD_CALCULATED_AT = "calculated_at";
    String FIELD_END = "end";
    String FIELD_BEGIN = "begin";
    String FIELD_INDEX_NAME = "index_name";
    String FIELD_STREAM_IDS = "stream_ids";
    Comparator<IndexRange> COMPARATOR = new IndexRangeComparator();

    String indexName();

    DateTime begin();

    DateTime end();

    DateTime calculatedAt();

    int calculationDuration();

    List<String> streamIds();
}