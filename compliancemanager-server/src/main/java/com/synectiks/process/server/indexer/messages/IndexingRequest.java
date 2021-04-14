/*
 * */
package com.synectiks.process.server.indexer.messages;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.indexer.IndexSet;

import javax.validation.constraints.NotNull;

@AutoValue
@JsonAutoDetect
public abstract class IndexingRequest {
    public abstract IndexSet indexSet();
    public abstract Indexable message();

    public static IndexingRequest create(@NotNull IndexSet indexSet, @NotNull Indexable message) {
        return new AutoValue_IndexingRequest(indexSet, message);
    }

}
