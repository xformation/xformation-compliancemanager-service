/*
 * */
package com.synectiks.process.server.indexer.cluster;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
@JsonAutoDetect
public abstract class PendingTasksStats {
    @JsonProperty
    public abstract int pendingTasks();

    @JsonProperty
    public abstract List<Long> pendingTasksTimeInQueue();

    public static PendingTasksStats create(int pendingTasks, List<Long> pendingTasksTimeInQueue) {
        return new AutoValue_PendingTasksStats(pendingTasks, pendingTasksTimeInQueue);
    }
}
