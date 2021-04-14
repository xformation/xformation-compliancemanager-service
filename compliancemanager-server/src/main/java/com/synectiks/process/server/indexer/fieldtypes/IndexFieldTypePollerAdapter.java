/*
 * */
package com.synectiks.process.server.indexer.fieldtypes;

import com.codahale.metrics.Timer;

import java.util.Optional;
import java.util.Set;

public interface IndexFieldTypePollerAdapter {
    Optional<Set<FieldTypeDTO>> pollIndex(String indexName, Timer pollTimer);
}
