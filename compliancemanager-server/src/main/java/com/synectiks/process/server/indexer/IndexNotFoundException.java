/*
 * */
package com.synectiks.process.server.indexer;

import java.util.Collections;
import java.util.List;

public class IndexNotFoundException extends ElasticsearchException {
    public IndexNotFoundException(String message) {
        super(message);
    }

    public IndexNotFoundException(String message, List<String> errorDetails) {
        super(message, errorDetails);
    }

    public static IndexNotFoundException create(String errorMessage, String index) {
        return new IndexNotFoundException(errorMessage, Collections.singletonList("Index not found for query: " + index + ". Try recalculating your index ranges."));
    }

}
