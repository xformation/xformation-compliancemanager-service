/*
 * */
package com.synectiks.process.server.system.stats.elasticsearch;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ClusterStats {
    public abstract String clusterName();

    public abstract String clusterVersion();

    public abstract NodesStats nodesStats();

    public abstract IndicesStats indicesStats();

    public static ClusterStats create(String clusterName, String clusterVersion, NodesStats nodesStats,
                                      IndicesStats indicesStats) {
        return new AutoValue_ClusterStats(clusterName, clusterVersion, nodesStats, indicesStats);
    }
}
