/*
 * */
package com.synectiks.process.server.rest.models.system.indexer.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.List;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class IndexInfo {
    @JsonProperty
    public abstract IndexStats primaryShards();

    @JsonProperty
    public abstract IndexStats allShards();

    @JsonProperty
    public abstract List<ShardRouting> routing();

    @JsonProperty
    public abstract boolean isReopened();

    @JsonCreator
    public static IndexInfo create(@JsonProperty("primary_shards") IndexStats primaryShards,
                                   @JsonProperty("all_shards") IndexStats allShards,
                                   @JsonProperty("routing") List<ShardRouting> routing,
                                   @JsonProperty("is_reopened") boolean isReopened) {
        return new AutoValue_IndexInfo(primaryShards, allShards, routing, isReopened);
    }
}
