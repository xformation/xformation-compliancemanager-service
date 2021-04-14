/*
 * */
package com.synectiks.process.server.indexer.indices;

import java.util.Set;

import com.synectiks.process.server.indexer.ElasticsearchException;

// TODO: This should actually be a `TooManyIndicesForAliasException`?
public class TooManyAliasesException extends ElasticsearchException {
    private final Set<String> indices;

    public TooManyAliasesException(final Set<String> indices) {
        super("More than one index in deflector alias: " + indices.toString());
        this.indices = indices;
    }

    public Set<String> getIndices() {
        return indices;
    }
}
