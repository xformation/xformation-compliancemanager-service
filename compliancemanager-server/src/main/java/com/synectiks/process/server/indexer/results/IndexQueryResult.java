/*
 * */
package com.synectiks.process.server.indexer.results;

public class IndexQueryResult {
    private final String originalQuery;
    private final long tookMs;
    private final String builtQuery;

    public IndexQueryResult(String originalQuery, String builtQuery, long tookMs) {
        this.originalQuery = originalQuery;
        this.tookMs = tookMs;
        this.builtQuery = builtQuery;
    }

    public String getOriginalQuery() {
        return originalQuery;
    }

    public String getBuiltQuery() {
        return builtQuery;
    }

    public long tookMs() {
        return tookMs;
    }
}
