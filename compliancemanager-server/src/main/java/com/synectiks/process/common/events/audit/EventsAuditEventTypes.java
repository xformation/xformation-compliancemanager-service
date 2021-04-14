/*
 * */
package com.synectiks.process.common.events.audit;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.audit.PluginAuditEventTypes;

import java.util.Set;

public class EventsAuditEventTypes implements PluginAuditEventTypes {
    private static final String EVENT_DEFINITON_PREFIX = "events:definition:";
    private static final String EVENT_NOTIFICATION_PREFIX = "events:notification:";

    public static final String EVENT_DEFINITION_CREATE = EVENT_DEFINITON_PREFIX + "create";
    public static final String EVENT_DEFINITION_DELETE = EVENT_DEFINITON_PREFIX + "delete";
    public static final String EVENT_DEFINITION_EXECUTE = EVENT_DEFINITON_PREFIX + "execute";
    public static final String EVENT_DEFINITION_UPDATE = EVENT_DEFINITON_PREFIX + "update";
    public static final String EVENT_NOTIFICATION_CREATE = EVENT_NOTIFICATION_PREFIX + "create";
    public static final String EVENT_NOTIFICATION_DELETE = EVENT_NOTIFICATION_PREFIX + "delete";
    public static final String EVENT_NOTIFICATION_UPDATE = EVENT_NOTIFICATION_PREFIX + "update";

    private static final ImmutableSet<String> EVENT_TYPES = ImmutableSet.<String>builder()
        .add(EVENT_DEFINITION_CREATE)
        .add(EVENT_DEFINITION_DELETE)
        .add(EVENT_DEFINITION_EXECUTE)
        .add(EVENT_DEFINITION_UPDATE)
        .add(EVENT_NOTIFICATION_CREATE)
        .add(EVENT_NOTIFICATION_DELETE)
        .add(EVENT_NOTIFICATION_UPDATE)
        .build();

    @Override
    public Set<String> auditEventTypes() {
        return EVENT_TYPES;
    }
}
