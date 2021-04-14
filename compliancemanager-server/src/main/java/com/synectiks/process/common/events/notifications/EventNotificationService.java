/*
 * */
package com.synectiks.process.common.events.notifications;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.events.configuration.EventsConfigurationProvider;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.MessageSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class EventNotificationService {
    private static final Logger LOG = LoggerFactory.getLogger(EventNotificationService.class);

    private final EventBacklogService eventBacklogService;
    private final EventsConfigurationProvider configurationProvider;

    @Inject
    public EventNotificationService(EventBacklogService eventBacklogService,
                                    EventsConfigurationProvider configurationProvider) {
        this.eventBacklogService = eventBacklogService;
        this.configurationProvider = configurationProvider;
    }

    public ImmutableList<MessageSummary> getBacklogForEvent(EventNotificationContext ctx) {
        final ImmutableList<MessageSummary> backlog;
        try {
            if (ctx.eventDefinition().isPresent()) {
                final long backlogSize = ctx.eventDefinition().get().notificationSettings().backlogSize();
                if (backlogSize <= 0) {
                    return ImmutableList.of();
                }
                backlog = eventBacklogService.getMessagesForEvent(ctx.event(), backlogSize);
            } else {
                backlog = eventBacklogService.getMessagesForEvent(ctx.event(), configurationProvider.get().eventNotificationsBacklog());
            }
        } catch (NotFoundException e) {
            LOG.error("Failed to fetch backlog for event {}", ctx.event().id());
            return ImmutableList.of();
        }
        return backlog;
    }
}
