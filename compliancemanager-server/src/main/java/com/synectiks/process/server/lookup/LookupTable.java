/*
 * */
package com.synectiks.process.server.lookup;

import com.google.auto.value.AutoValue;
import com.google.common.collect.Streams;
import com.synectiks.process.server.plugin.lookup.LookupCache;
import com.synectiks.process.server.plugin.lookup.LookupCacheKey;
import com.synectiks.process.server.plugin.lookup.LookupDataAdapter;
import com.synectiks.process.server.plugin.lookup.LookupResult;

import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Streams.stream;

/**
 * A LookupTable references a {@link LookupCache} and a {@link LookupDataAdapter}, which both have their own lifecycle.
 * <p>
 * Multiple lookup tables can use the same caches and adapters.
 */
@AutoValue
@WithBeanGetter
public abstract class LookupTable {

    @Nullable
    public abstract String id();

    public abstract String title();

    public abstract String description();

    public abstract String name();

    public abstract LookupCache cache();

    public abstract LookupDataAdapter dataAdapter();

    public abstract LookupDefaultSingleValue defaultSingleValue();

    public abstract LookupDefaultMultiValue defaultMultiValue();

    public static Builder builder() {
        return new AutoValue_LookupTable.Builder();
    }

    @Nullable
    public String error() {
        return Streams.concat(stream(dataAdapter().getError()), stream(cache().getError()))
                .map(Throwable::getMessage)
                .collect(Collectors.joining("\n"));
    }

    @Nullable
    public LookupResult lookup(@Nonnull Object key) {
        final LookupResult result = cache().get(LookupCacheKey.create(dataAdapter(), key), () -> dataAdapter().get(key));

        if (result.hasError()) {
            return result;
        }
        // The default value will only be used if single, multi and list values are empty
        if (result.isEmpty()) {
            return LookupResult.addDefaults(defaultSingleValue(), defaultMultiValue()).hasError(result.hasError()).build();
        }
        return result;
    }

    public LookupResult setValue(@Nonnull Object key, @Nonnull Object value) {
        final LookupResult result = dataAdapter().setValue(key, value);
        cache().purge(LookupCacheKey.create(dataAdapter(), key));
        return result;
    }

    public LookupResult setStringList(@Nonnull Object key, @Nonnull List<String> value) {
        final LookupResult result = dataAdapter().setStringList(key, value);
        cache().purge(LookupCacheKey.create(dataAdapter(), key));
        return result;
    }

    public LookupResult addStringList(@Nonnull Object key, @Nonnull List<String> value, boolean keepDuplicates) {
        final LookupResult result = dataAdapter().addStringList(key, value, keepDuplicates);
        cache().purge(LookupCacheKey.create(dataAdapter(), key));
        return result;
    }

    public LookupResult removeStringList(@Nonnull Object key, @Nonnull List<String> value) {
        final LookupResult result = dataAdapter().removeStringList(key, value);
        cache().purge(LookupCacheKey.create(dataAdapter(), key));
        return result;
    }

    public void clearKey(@Nonnull Object key) {
        dataAdapter().clearKey(key);
        cache().purge(LookupCacheKey.create(dataAdapter(), key));
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(String id);

        public abstract Builder title(String title);

        public abstract Builder description(String description);

        public abstract Builder name(String name);

        public abstract Builder cache(LookupCache cache);

        public abstract Builder dataAdapter(LookupDataAdapter dataAdapter);

        public abstract Builder defaultSingleValue(LookupDefaultSingleValue defaultSingleValue);

        public abstract Builder defaultMultiValue(LookupDefaultMultiValue defaultMultiValue);

        public abstract LookupTable build();
    }
}
