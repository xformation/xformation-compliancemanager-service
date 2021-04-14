/*
 * */
package com.synectiks.process.server.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static com.codahale.metrics.MetricRegistry.name;

public class CacheStatsSet implements MetricSet {
    private final Map<String, Metric> metrics;

    public CacheStatsSet(final String prefix, final Cache cache) {
        this.metrics = ImmutableMap.<String, Metric>builder()
                .put(name(prefix, "requests"), new Gauge<Long>() {
                    @Override
                    public Long getValue() {
                        return cache.stats().requestCount();
                    }
                })
                .put(name(prefix, "hits"), new Gauge<Long>() {
                    @Override
                    public Long getValue() {
                        return cache.stats().hitCount();
                    }
                })
                .put(name(prefix, "misses"), new Gauge<Long>() {
                    @Override
                    public Long getValue() {
                        return cache.stats().missCount();
                    }
                })
                .put(name(prefix, "evictions"), new Gauge<Long>() {
                    @Override
                    public Long getValue() {
                        return cache.stats().evictionCount();
                    }
                })
                .put(name(prefix, "total-load-time-ns"), new Gauge<Long>() {
                    @Override
                    public Long getValue() {
                        return cache.stats().totalLoadTime();
                    }
                })
                .put(name(prefix, "load-successes"), new Gauge<Long>() {
                    @Override
                    public Long getValue() {
                        return cache.stats().loadSuccessCount();
                    }
                })
                .put(name(prefix, "load-exceptions"), new Gauge<Long>() {
                    @Override
                    public Long getValue() {
                        return cache.stats().loadExceptionCount();
                    }
                })
                .put(name(prefix, "hit-rate"), new Gauge<Double>() {
                    @Override
                    public Double getValue() {
                        return cache.stats().hitRate();
                    }
                })
                .put(name(prefix, "miss-rate"), new Gauge<Double>() {
                    @Override
                    public Double getValue() {
                        return cache.stats().missRate();
                    }
                })
                .build();
    }

    @Override
    public Map<String, Metric> getMetrics() {
        return metrics;
    }
}
