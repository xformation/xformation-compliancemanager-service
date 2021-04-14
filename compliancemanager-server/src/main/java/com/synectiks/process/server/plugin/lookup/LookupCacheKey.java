/*
 * */
package com.synectiks.process.server.plugin.lookup;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;

/**
 * This class is used as a key in {@link LookupCache} implementations.
 * <p>
 * It combines the actual key object and a prefix value to allow the same key to be cached for different data adapters
 * without overwriting each other.
 * <p>
 * Using a {@link LookupCacheKey} with only a prefix might be used for operations like purging all cache keys for
 * the given prefix.
 * <p>
 * Examples:
 * <pre>{@code
 * // Key with prefix and key
 * LookupCacheKey.create(dataAdapter.id(), "foo");
 *
 * // Key with prefix only
 * LookupCacheKey.prefix(dataAdapter.id());
 * }</pre>
 * <p>
 * For convenience, this class can be serialized and deserialized with Jackson (see
 * {@link com.fasterxml.jackson.databind.ObjectMapper}, but we strongly recommend implementing your own
 * serialization and deserialization logic if you're implementing a lookup cache.
 * <p>
 * There are <em>no guarantees</em> about binary compatibility of this class across alertmanager releases!
 */
@AutoValue
public abstract class LookupCacheKey {
    @JsonProperty("prefix")
    public abstract String prefix();

    @JsonProperty("key")
    @Nullable
    public abstract Object key();

    @JsonCreator
    @SuppressWarnings("unused")
    public static LookupCacheKey createFromJSON(@JsonProperty("prefix") String prefix, @JsonProperty("key") @Nullable Object key) {
        return new AutoValue_LookupCacheKey(prefix, key);
    }

    public static LookupCacheKey create(LookupDataAdapter adapter, @Nullable Object key) {
        return new AutoValue_LookupCacheKey(adapter.id(), key);
    }

    public static LookupCacheKey prefix(LookupDataAdapter adapter) {
        return create(adapter, null);
    }

    /**
     * If the cache key instance does not have a key object, this returns true.
     * <p>
     * A cache key with only a prefix can be used to operate on all keys for the given prefix. (e.g. cache purge)
     *
     * @return true if there is no key object, false otherwise
     */
    @JsonIgnore
    public boolean isPrefixOnly() {
        return key() == null;
    }
}