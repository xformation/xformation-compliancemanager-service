/*
 * */
package com.synectiks.process.common.events.contentpack.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.events.notifications.NotificationDto;
import com.synectiks.process.server.contentpacks.NativeEntityConverter;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;

import java.util.Map;

@AutoValue
@JsonDeserialize(builder = NotificationEntity.Builder.class)
public abstract class NotificationEntity implements NativeEntityConverter<NotificationDto> {

    public static final String FIELD_TITLE = "title";
    public static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_CONFIG = "config";

    public static Builder builder() {
        return Builder.create();
    }

    @JsonProperty(FIELD_TITLE)
    public abstract ValueReference title();

    @JsonProperty(FIELD_DESCRIPTION)
    public abstract ValueReference description();

    @JsonProperty(FIELD_CONFIG)
    public abstract EventNotificationConfigEntity config();

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public static abstract class Builder {

        @JsonCreator
        public static Builder create() {
            return new AutoValue_NotificationEntity.Builder();
        }

        @JsonProperty(FIELD_TITLE)
        public abstract Builder title(ValueReference title);

        @JsonProperty(FIELD_DESCRIPTION)
        public abstract Builder description(ValueReference description);

        @JsonProperty(FIELD_CONFIG)
        public abstract Builder config(EventNotificationConfigEntity config);

        public abstract NotificationEntity build();
    }

    @Override
    public NotificationDto toNativeEntity(Map<String, ValueReference> parameters, Map<EntityDescriptor, Object> nativeEntities) {
        return NotificationDto.builder()
                .description(description().asString(parameters))
                .title(title().asString(parameters))
                .config(config().toNativeEntity(parameters, nativeEntities))
                .build();
    }
}
