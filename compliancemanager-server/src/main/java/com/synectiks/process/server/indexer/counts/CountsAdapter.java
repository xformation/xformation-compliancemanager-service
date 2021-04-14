/*
 * */
package com.synectiks.process.server.indexer.counts;

import java.util.List;

public interface CountsAdapter {
    long totalCount(List<String> indices);
}
