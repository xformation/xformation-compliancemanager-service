/*
 * */
package com.synectiks.process.server.lookup;

import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import com.synectiks.process.server.plugin.lookup.LookupCache;
import com.synectiks.process.server.plugin.lookup.LookupCacheKey;
import com.synectiks.process.server.plugin.lookup.LookupCachePurge;
import com.synectiks.process.server.plugin.lookup.LookupDataAdapter;

/**
 * This will be passed to {@link LookupDataAdapter#refresh(LookupCachePurge)} to allow data adapters to purge
 * the cache after updating their state/data. It takes care of using the correct {@link LookupCacheKey} prefix
 * to delete only those cache keys which belong to the data adapter.
 */
public class CachePurge implements LookupCachePurge {
    private final ConcurrentMap<String, LookupTable> tables;
    private final LookupDataAdapter adapter;

    public CachePurge(ConcurrentMap<String, LookupTable> tables, LookupDataAdapter adapter) {
        this.tables = tables;
        this.adapter = adapter;
    }

    @Override
    public void purgeAll() {
        // Collect related caches on every call to improve the chance that we get all of them
        caches().forEach(cache -> cache.purge(LookupCacheKey.prefix(adapter)));
    }

    @Override
    public void purgeKey(Object key) {
        // Collect related caches on every call to improve the chance that we get all of them
        caches().forEach(cache -> cache.purge(LookupCacheKey.create(adapter, key)));
    }

    private Stream<LookupCache> caches() {
        return tables.values().stream()
                .filter(table -> table.dataAdapter().id().equals(adapter.id()))
                .map(LookupTable::cache);
    }
}
