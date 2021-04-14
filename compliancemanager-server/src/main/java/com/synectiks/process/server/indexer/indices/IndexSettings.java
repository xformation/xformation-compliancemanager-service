/*
 * */
package com.synectiks.process.server.indexer.indices;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonAutoDetect
public abstract class IndexSettings {
    public abstract int shards();
    public abstract int replicas();

    public static IndexSettings create(int shards, int replicas) {
        return new AutoValue_IndexSettings(shards, replicas);
    }
}
