/*
 * */
package com.synectiks.process.server.indexer.results;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.synectiks.process.server.indexer.ranges.IndexRange;
import com.synectiks.process.server.plugin.Message;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SearchResult extends IndexQueryResult {

	private final long totalResults;
	private final List<ResultMessage> results;
	private final Set<String> fields;
    private final Set<IndexRange> usedIndices;

	public SearchResult(List<ResultMessage> hits, long totalResults, Set<IndexRange> usedIndices, String originalQuery, String builtQuery, long tookMs) {
	    super(originalQuery, builtQuery, tookMs);
	    this.results = hits;
        this.fields = extractFields(hits);
        this.totalResults = totalResults;
        this.usedIndices = usedIndices;
    }

    private SearchResult(String query, String originalQuery) {
        super(query, originalQuery, 0);
        this.results = Collections.emptyList();
        this.fields = Collections.emptySet();
        this.usedIndices = Collections.emptySet();
        this.totalResults = 0;
    }

    public long getTotalResults() {
		return totalResults;
	}

	public List<ResultMessage> getResults() {
		return results;
	}

	public Set<String> getFields() {
		return fields;
	}

	@VisibleForTesting
    Set<String> extractFields(List<ResultMessage> hits) {
        Set<String> filteredFields = Sets.newHashSet();

        hits.forEach(hit -> {
            final Message message = hit.getMessage();
            for (String field : message.getFieldNames()) {
                if (!Message.FILTERED_FIELDS.contains(field)) {
                    filteredFields.add(field);
                }
            }
        });

        return filteredFields;
    }

    public Set<IndexRange> getUsedIndices() {
        return usedIndices;
    }

    public static SearchResult empty(String query, String originalQuery) {
        return new SearchResult(query, originalQuery);
    }
}
