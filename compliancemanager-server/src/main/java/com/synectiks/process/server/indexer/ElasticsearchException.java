/*
 * */
package com.synectiks.process.server.indexer;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import joptsimple.internal.Strings;

import java.util.Collections;
import java.util.List;

/**
 * {@code ElasticsearchException} is the superclass of those
 * exceptions that can be thrown during the normal interaction
 * with Elasticsearch.
 */
public class ElasticsearchException extends RuntimeException {
    private final List<String> errorDetails;

    public ElasticsearchException() {
        super();
        this.errorDetails = Collections.emptyList();
    }

    public ElasticsearchException(String message) {
        super(message);
        this.errorDetails = Collections.emptyList();
    }

    public ElasticsearchException(String message, Throwable cause) {
        super(message, cause);
        this.errorDetails = Collections.emptyList();
    }

    public ElasticsearchException(Throwable cause) {
        super(cause);
        this.errorDetails = Collections.emptyList();
    }

    public ElasticsearchException(String message, List<String> errorDetails) {
        super(message);
        this.errorDetails = ImmutableList.copyOf(errorDetails);
    }

    public ElasticsearchException(String message, List<String> errorDetails, Throwable cause) {
        super(message, cause);
        this.errorDetails = ImmutableList.copyOf(errorDetails);
    }

    public ElasticsearchException(List<String> errorDetails, Throwable cause) {
        super(cause);
        this.errorDetails = ImmutableList.copyOf(errorDetails);
    }

    public List<String> getErrorDetails() {
        return errorDetails;
    }

    @Override
    public String getMessage() {
        final StringBuilder sb = new StringBuilder(super.getMessage());

        if(!errorDetails.isEmpty()) {
            sb.append("\n\n");
            errorDetails.forEach(sb::append);
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("message", getMessage())
                .add("errorDetails", getErrorDetails())
                .toString();
    }
}
