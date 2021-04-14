/*
 * */
package com.synectiks.process.server.indexer.results;

import java.io.IOException;
import java.util.List;

public interface ScrollResult {
    ScrollChunk nextChunk() throws IOException;

    String getQueryHash();

    long totalHits();

    void cancel() throws IOException;

    long tookMs();

    interface ScrollChunk {
        List<String> getFields();

        int getChunkNumber();

        default boolean isFirstChunk() {
            return getChunkNumber() == 0;
        }

        List<ResultMessage> getMessages();
    }
}
