/*
 * */
package com.synectiks.process.server.plugin;

import com.codahale.metrics.MetricRegistry;

// TODO this is a stupid workaround to have both a Singleton MetricRegistry and be able to inject new instances for local usage.
public class LocalMetricRegistry extends MetricRegistry {
}
