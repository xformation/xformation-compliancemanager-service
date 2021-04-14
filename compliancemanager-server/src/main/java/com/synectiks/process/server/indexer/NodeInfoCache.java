/*
 * */
package com.synectiks.process.server.indexer;

import com.github.joschi.jadconfig.util.Duration;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.synectiks.process.server.indexer.cluster.Cluster;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class NodeInfoCache {
    private static final Duration EXPIRE_DURATION = Duration.minutes(1L);
    
    private final LoadingCache<String, Optional<String>> nodeNameCache;
    private final LoadingCache<String, Optional<String>> hostNameCache;

    @Inject
    public NodeInfoCache(Cluster cluster) {
        this.nodeNameCache = CacheBuilder.newBuilder()
                .expireAfterWrite(EXPIRE_DURATION.getQuantity(), EXPIRE_DURATION.getUnit())
                .build(CacheLoader.from(cluster::nodeIdToName));
        this.hostNameCache = CacheBuilder.newBuilder()
                .expireAfterWrite(EXPIRE_DURATION.getQuantity(), EXPIRE_DURATION.getUnit())
                .build(CacheLoader.from(cluster::nodeIdToHostName));
    }

    public Optional<String> getNodeName(String nodeId) {
        return nodeNameCache.getUnchecked(nodeId);
    }

    public Optional<String> getHostName(String nodeId) {
        return hostNameCache.getUnchecked(nodeId);
    }
}