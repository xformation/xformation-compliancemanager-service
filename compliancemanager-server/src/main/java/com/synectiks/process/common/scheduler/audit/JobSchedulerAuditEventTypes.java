/*
 * */
package com.synectiks.process.common.scheduler.audit;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.audit.PluginAuditEventTypes;

import java.util.Set;

public class JobSchedulerAuditEventTypes implements PluginAuditEventTypes {
    public static final String SCHEDULER_JOB_CREATE = "scheduler:job:create";
    public static final String SCHEDULER_JOB_DELETE = "scheduler:job:delete";
    public static final String SCHEDULER_JOB_UPDATE = "scheduler:job:update";
    public static final String SCHEDULER_TRIGGER_CREATE = "scheduler:trigger:create";

    private static final ImmutableSet<String> EVENT_TYPES = ImmutableSet.<String>builder()
        .add(SCHEDULER_JOB_CREATE)
        .add(SCHEDULER_JOB_DELETE)
        .add(SCHEDULER_JOB_UPDATE)
        .add(SCHEDULER_TRIGGER_CREATE)
        .build();

    @Override
    public Set<String> auditEventTypes() {
        return EVENT_TYPES;
    }
}
