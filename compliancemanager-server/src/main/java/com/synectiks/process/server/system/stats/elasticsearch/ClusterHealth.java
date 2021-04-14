/*
 * */
package com.synectiks.process.server.system.stats.elasticsearch;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.indexer.cluster.PendingTasksStats;

import org.graylog.autovalue.WithBeanGetter;

import java.util.List;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class ClusterHealth {
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

    @JsonProperty
    public abstract int pendingTasks();

    @JsonProperty
    public abstract List<Long> pendingTasksTimeInQueue();

    public static ClusterHealth from(ShardStats shardStats, PendingTasksStats pendingTasksStats) {
        return create(
                shardStats.numberOfNodes(),
                shardStats.numberOfDataNodes(),
                shardStats.activeShards(),
                shardStats.relocatingShards(),
                shardStats.activePrimaryShards(),
                shardStats.initializingShards(),
                shardStats.unassignedShards(),
                shardStats.timedOut(),
                pendingTasksStats.pendingTasks(),
                pendingTasksStats.pendingTasksTimeInQueue()
        );
    }

    public static ClusterHealth create(int numberOfNodes,
                                       int numberOfDataNodes,
                                       int activeShards,
                                       int relocatingShards,
                                       int activePrimaryShards,
                                       int initializingShards,
                                       int unassignedShards,
                                       boolean timedOut,
                                       int pendingTasks,
                                       List<Long> pendingTasksTimeInQueue) {
        return new AutoValue_ClusterHealth(numberOfNodes, numberOfDataNodes, activeShards, relocatingShards,
                activePrimaryShards, initializingShards, unassignedShards, timedOut,
                pendingTasks, pendingTasksTimeInQueue);
    }
}
