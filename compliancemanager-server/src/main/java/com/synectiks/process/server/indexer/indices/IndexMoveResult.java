/*
 * */
package com.synectiks.process.server.indexer.indices;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonAutoDetect
public abstract class IndexMoveResult {
    public abstract int movedDocuments();
    public abstract long tookMs();
    public abstract boolean hasFailedItems();

    public static IndexMoveResult create(int movedDocuments, long tookMs, boolean hasFailedItems) {
        return new AutoValue_IndexMoveResult(movedDocuments, tookMs, hasFailedItems);
    }
}
