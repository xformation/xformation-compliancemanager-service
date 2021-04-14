/*
 * */
package com.synectiks.process.server.indexer.cluster;

import com.synectiks.process.server.indexer.cluster.health.ClusterAllocationDiskSettings;
import com.synectiks.process.server.indexer.cluster.health.NodeDiskUsageStats;
import com.synectiks.process.server.indexer.cluster.health.NodeFileDescriptorStats;
import com.synectiks.process.server.indexer.indices.HealthStatus;
import com.synectiks.process.server.rest.models.system.indexer.responses.ClusterHealth;
import com.synectiks.process.server.system.stats.elasticsearch.ClusterStats;
import com.synectiks.process.server.system.stats.elasticsearch.ShardStats;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface ClusterAdapter {
    Optional<HealthStatus> health(Collection<String> indices);

    Set<NodeFileDescriptorStats> fileDescriptorStats();

    Set<NodeDiskUsageStats> diskUsageStats();

    ClusterAllocationDiskSettings clusterAllocationDiskSettings();

    Optional<String> nodeIdToName(String nodeId);

    Optional<String> nodeIdToHostName(String nodeId);

    boolean isConnected();

    Optional<String> clusterName(Collection<String> indices);

    Optional<ClusterHealth> clusterHealthStats(Collection<String> indices);

    ClusterStats clusterStats();

    PendingTasksStats pendingTasks();

    ShardStats shardStats(Collection<String> indices);
}
