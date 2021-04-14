/*
 * */
package com.synectiks.process.common.events.contentpack.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.events.notifications.EventNotificationHandler;
import com.synectiks.process.common.events.notifications.NotificationDto;
import com.synectiks.process.common.events.notifications.NotificationParameters;
import com.synectiks.process.server.contentpacks.NativeEntityConverter;
import com.synectiks.process.server.contentpacks.exceptions.ContentPackException;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = EventNotificationHandlerConfigEntity.Builder.class)
public abstract class EventNotificationHandlerConfigEntity implements NativeEntityConverter<EventNotificationHandler.Config> {

    private static final String FIELD_NOTIFICATION_ID = "notification_id";
    private static final String FIELD_NOTIFICATION_PARAMETERS = "notification_parameters";

    @JsonProperty(FIELD_NOTIFICATION_ID)
    public abstract ValueReference notificationId();

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
            return new AutoValue_EventNotificationHandlerConfigEntity.Builder();
        }

        @JsonProperty(FIELD_NOTIFICATION_ID)
        public abstract Builder notificationId(ValueReference notificationId);

        @JsonProperty(FIELD_NOTIFICATION_PARAMETERS)
        public abstract Builder notificationParameters(@Nullable NotificationParameters notificationParameters);

        public abstract EventNotificationHandlerConfigEntity build();
    }

    @Override
    public EventNotificationHandler.Config toNativeEntity(Map<String, ValueReference> parameters, Map<EntityDescriptor, Object> natvieEntities) {
        String notificationId = notificationId().asString(parameters);
        final EntityDescriptor notificationDescriptor = EntityDescriptor.create(notificationId, ModelTypes.NOTIFICATION_V1);
        final Object notification = natvieEntities.get(notificationDescriptor);

        final EventNotificationHandler.Config.Builder configBuilder = EventNotificationHandler.Config.builder();

        if (notification == null) {
            throw new ContentPackException("Missing notification (" + notificationId + ") for event definition");
        } else if (notification instanceof NotificationDto) {
            NotificationDto notificationDto = (NotificationDto) notification;
            configBuilder.notificationId(notificationDto.id());
        } else {
            throw new ContentPackException("Invalid type for notification (" + notificationId + ") of event definition: " + notification.getClass());
        }

        return configBuilder.notificationParameters(notificationParameters().orElse(null))
                .build();
    }
}
