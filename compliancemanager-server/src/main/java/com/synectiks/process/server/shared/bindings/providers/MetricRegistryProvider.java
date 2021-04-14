/*
 * */
package com.synectiks.process.server.shared.bindings.providers;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;

import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.management.ManagementFactory;

@Singleton
public class MetricRegistryProvider implements Provider<MetricRegistry> {
    private final MetricRegistry metricRegistry;

    public MetricRegistryProvider() {
        this.metricRegistry = new MetricRegistry();

        metricRegistry.register("jvm.buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
        metricRegistry.register("jvm.cl", new ClassLoadingGaugeSet());
        metricRegistry.register("jvm.gc", new GarbageCollectorMetricSet());
        metricRegistry.register("jvm.memory", new MemoryUsageGaugeSet());
        metricRegistry.register("jvm.threads", new ThreadStatesGaugeSet());
    }

    @Override
    public MetricRegistry get() {
        return metricRegistry;
    }
}
