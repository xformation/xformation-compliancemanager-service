/*
 * */
package com.synectiks.process.server.indexer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class QueryParsingException extends ElasticsearchException {
    private final Integer line;
    private final Integer column;
    private final String index;

    public QueryParsingException(String message, Integer line, Integer column, String index) {
        this(message, line, column, index, Collections.emptyList());
    }

    public QueryParsingException(String message, Integer line, Integer column, String index, List<String> errorDetails) {
        super(message, errorDetails);
        this.line = line;
        this.column = column;
        this.index = index;
    }

    public Optional<Integer> getLine() {
        return Optional.ofNullable(line);
    }

    public Optional<Integer> getColumn() {
        return Optional.ofNullable(column);
    }

    public Optional<String> getIndex() {
        return Optional.ofNullable(index);
    }
}
