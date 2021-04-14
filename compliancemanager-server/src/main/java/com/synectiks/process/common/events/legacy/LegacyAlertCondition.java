/*
 * */
package com.synectiks.process.common.events.legacy;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.common.events.event.EventDto;
import com.synectiks.process.common.events.processor.EventDefinition;
import com.synectiks.process.server.alerts.AbstractAlertCondition;
import com.synectiks.process.server.plugin.alarms.AlertCondition;
import com.synectiks.process.server.plugin.streams.Stream;

/**
 * This is used to support legacy {@link com.synectiks.process.server.plugin.alarms.callbacks.AlarmCallback}s. An alarm callback
 * expects an instance of an {@link AlertCondition}. This is basically just a small wrapper around
 * {@link AbstractAlertCondition} to act as a dummy.
 */
public class LegacyAlertCondition extends AbstractAlertCondition {
    private final String description;

    LegacyAlertCondition(Stream stream,
                         EventDefinition eventDefinition,
                         EventDto eventDto) {
        super(
                stream,
                eventDefinition.id(),
                eventDefinition.config().type(),
                eventDto.processingTimestamp(),
                "admin",
                ImmutableMap.of("backlog", eventDefinition.notificationSettings().backlogSize()),
                eventDefinition.title()
        );
        this.description = eventDefinition.title();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public AlertCondition.CheckResult runCheck() {
        throw new UnsupportedOperationException("Running LegacyAlertCondition is not supported!");
    }
}
