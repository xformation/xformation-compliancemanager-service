/*
 * */
package com.synectiks.process.server.bindings.providers;

import com.codahale.metrics.MetricRegistry;
import com.synectiks.process.server.shared.system.activities.ActivityWriter;
import com.synectiks.process.server.system.jobs.SystemJobManager;

import javax.inject.Inject;
import javax.inject.Provider;

public class SystemJobManagerProvider implements Provider<SystemJobManager> {
    private static SystemJobManager systemJobManager = null;

    @Inject
    public SystemJobManagerProvider(ActivityWriter activityWriter, MetricRegistry metricRegistry) {
        if (systemJobManager == null)
            systemJobManager = new SystemJobManager(activityWriter, metricRegistry);
    }

    @Override
    public SystemJobManager get() {
        return systemJobManager;
    }
}
