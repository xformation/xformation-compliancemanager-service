/*
 * */
package com.synectiks.process.server.plugin.lookup;

/**
 * This is passed into {@link LookupDataAdapter#doRefresh(LookupCachePurge)} to allow data adapters to prune cache
 * entries without having to know about the actual cache instances.
 */
public interface LookupCachePurge {
    /**
     * Purges all entries from the cache.
     */
    void purgeAll();

    /**
     * Purges only the cache entry for the given key.
     * @param key cache key to purge
     */
    void purgeKey(Object key);
}
