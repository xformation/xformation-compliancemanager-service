/*
 * */
package com.synectiks.process.common.events.notifications;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.events.contentpack.entities.EventNotificationHandlerConfigEntity;
import com.synectiks.process.common.events.event.Event;
import com.synectiks.process.common.events.event.EventWithContext;
import com.synectiks.process.common.events.processor.EventDefinition;
import com.synectiks.process.common.scheduler.DBJobDefinitionService;
import com.synectiks.process.common.scheduler.DBJobTriggerService;
import com.synectiks.process.common.scheduler.JobDefinitionDto;
import com.synectiks.process.common.scheduler.JobTriggerDto;
import com.synectiks.process.common.scheduler.schedule.OnceJobSchedule;
import com.synectiks.process.server.contentpacks.ContentPackable;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.database.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class EventNotificationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(EventNotificationHandler.class);

    private final DBJobDefinitionService jobDefinitionService;
    private final DBJobTriggerService jobTriggerService;
    private final DBNotificationService notificationService;
    private final NotificationGracePeriodService notificationGracePeriodService;

    @Inject
    public EventNotificationHandler(DBJobDefinitionService jobDefinitionService,
                                    DBJobTriggerService jobTriggerService,
                                    DBNotificationService notificationService,
                                    NotificationGracePeriodService notificationGracePeriodService) {
        this.jobDefinitionService = jobDefinitionService;
        this.jobTriggerService = jobTriggerService;
        this.notificationService = notificationService;
        this.notificationGracePeriodService = notificationGracePeriodService;
    }

    public void handleEvents(EventDefinition definition, List<EventWithContext> eventsWithContext) {
        for (Config config : definition.notifications()) {
            final Optional<JobDefinitionDto> jobDefinition =
                    jobDefinitionService.getByConfigField(Config.FIELD_NOTIFICATION_ID, config.notificationId());

            if (!jobDefinition.isPresent()) {
                LOG.error("Couldn't find job definition for notification <{}>", config.notificationId());
                continue;
            }

            final Optional<NotificationDto> notificationDto = notificationService.get(config.notificationId());
            if (!notificationDto.isPresent()) {
                LOG.error("Couldn't find notification definition for id <{}>", config.notificationId());
                continue;
            }
            final EventNotificationConfig notificationConfig = notificationDto.get().config();
            // TODO: The job trigger data needs information about the events and how to re-run the query to create the backlog

            for (EventWithContext eventWithContext : eventsWithContext) {
                final Event event = eventWithContext.event();
                if (notificationGracePeriodService.inGracePeriod(definition, config.notificationId(), event)) {
                    continue;
                }
                try {
                    final JobTriggerDto trigger = jobTriggerService.create(JobTriggerDto.builder()
                            .jobDefinitionId(jobDefinition.get().id())
                            .schedule(OnceJobSchedule.create())
                            .data(notificationConfig.toJobTriggerData(event.toDto()))
                            .build());
                    LOG.debug("Scheduled job <{}> for notification <{}> - event <{}/{}>", trigger.id(), config.notificationId(), event.getId(), event.getMessage());
                    // TODO: The trigger ID needs to be added to the "triggered_tasks" list of the event
                } catch (Exception e) {
                    LOG.error("Couldn't create job trigger for notification <{}> and event: {}", config.notificationId(), event, e);
                }
            }
        }
    }

    @AutoValue
    @JsonDeserialize(builder = Config.Builder.class)
    public static abstract class Config implements ContentPackable<EventNotificationHandlerConfigEntity> {
        private static final String FIELD_NOTIFICATION_ID = EventNotificationConfig.FIELD_NOTIFICATION_ID;
        private static final String FIELD_NOTIFICATION_PARAMETERS = "notification_parameters";

        @JsonProperty(FIELD_NOTIFICATION_ID)
        public abstract String notificationId();

        @JsonProperty(FIELD_NOTIFICATION_PARAMETERS)
        public abstract Optional<NotificationParameters> notificationParameters();


        public static Builder builder() {
            return Builder.create();
        }

        public abstract Builder toBuilder();

        @AutoValue.Builder
        public static abstract class Builder {
            @JsonCreator
            public static Builder create() {
                return new AutoValue_EventNotificationHandler_Config.Builder();
            }

            @JsonProperty(FIELD_NOTIFICATION_ID)
            public abstract Builder notificationId(String notificationId);

            @JsonProperty(FIELD_NOTIFICATION_PARAMETERS)
            public abstract Builder notificationParameters(@Nullable NotificationParameters notificationParameters);

            public abstract Config build();
        }

        @Override
        public EventNotificationHandlerConfigEntity toContentPackEntity(EntityDescriptorIds entityDescriptorIds) {
            final Optional<String> optionalNotification = entityDescriptorIds.get(notificationId(), ModelTypes.NOTIFICATION_V1);
            if (optionalNotification.isPresent()) {
                final String notificationId = optionalNotification.get();
                return EventNotificationHandlerConfigEntity.builder()
                    .notificationId(ValueReference.of(notificationId))
                    .notificationParameters(notificationParameters().orElse(null))
                    .build();
            }

            return EventNotificationHandlerConfigEntity.builder()
                .notificationParameters(notificationParameters().orElse(null))
                .build();
        }
    }
}
