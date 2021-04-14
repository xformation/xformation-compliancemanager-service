/*
 * */
package com.synectiks.process.server.system.stats.elasticsearch;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.indexer.indices.HealthStatus;

import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class ElasticsearchStats {
    @JsonProperty
    public abstract String clusterName();

    @JsonProperty
    public abstract String clusterVersion();

    @JsonProperty
    public abstract HealthStatus status();

    @JsonProperty
    public abstract ClusterHealth clusterHealth();

    @JsonProperty
    public abstract NodesStats nodesStats();

    @JsonProperty
    public abstract IndicesStats indicesStats();

    public static ElasticsearchStats create(String clusterName,
                                            String clusterVersion,
                                            HealthStatus status,
                                            ClusterHealth clusterHealth,
                                            NodesStats nodesStats,
                                            IndicesStats indicesStats) {
        return new AutoValue_ElasticsearchStats(clusterName, clusterVersion, status, clusterHealth, nodesStats, indicesStats);
    }
}
