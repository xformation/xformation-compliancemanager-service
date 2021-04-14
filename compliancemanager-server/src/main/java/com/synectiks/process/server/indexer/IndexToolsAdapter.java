/*
 * */
package com.synectiks.process.server.indexer;

import org.joda.time.DateTime;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface IndexToolsAdapter {
    Map<DateTime, Map<String, Long>> fieldHistogram(String fieldName, Set<String> indices, Optional<Set<String>> includedStreams, long interval);

    long count(Set<String> indices, Optional<Set<String>> includedStreams);
}
