/*
 * */
package com.synectiks.process.server.system.stats.elasticsearch;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonAutoDetect
public abstract class ShardStats {
    @JsonProperty
    public abstract int numberOfNodes();

    @JsonProperty
    public abstract int numberOfDataNodes();

    @JsonProperty
    public abstract int activeShards();

    @JsonProperty
    public abstract int relocatingShards();

    @JsonProperty
    public abstract int activePrimaryShards();

    @JsonProperty
    public abstract int initializingShards();

    @JsonProperty
    public abstract int unassignedShards();

    @JsonProperty
    public abstract boolean timedOut();

    public static ShardStats create(int numberOfNodes,
                                    int numberOfDataNodes,
                                    int activeShards,
                                    int relocatingShards,
                                    int activePrimaryShards,
                                    int initializingShards,
                                    int unassignedShards,
                                    boolean timedOut) {
        return new AutoValue_ShardStats(numberOfNodes, numberOfDataNodes, activeShards, relocatingShards,
                activePrimaryShards, initializingShards, unassignedShards, timedOut);
    }
}
