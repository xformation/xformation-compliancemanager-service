/*
 * */
package com.synectiks.process.common.events.notifications;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.events.contentpack.entities.EventNotificationConfigEntity;
import com.synectiks.process.common.events.contentpack.entities.NotificationEntity;
import com.synectiks.process.server.contentpacks.ContentPackable;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.plugin.rest.ValidationResult;

import org.mongojack.Id;
import org.mongojack.ObjectId;

import javax.annotation.Nullable;

@AutoValue
@JsonAutoDetect
@JsonDeserialize(builder = NotificationDto.Builder.class)
public abstract class NotificationDto implements ContentPackable {
    public static final String FIELD_ID = "id";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_CONFIG = "config";

    @Id
    @ObjectId
    @Nullable
    @JsonProperty(FIELD_ID)
    public abstract String id();

    @JsonProperty(FIELD_TITLE)
    public abstract String title();

    @JsonProperty(FIELD_DESCRIPTION)
    public abstract String description();

    @JsonProperty(FIELD_CONFIG)
    public abstract EventNotificationConfig config();

    public static Builder builder() {
        return Builder.create();
    }

    public abstract Builder toBuilder();

    @JsonIgnore
    public ValidationResult validate() {
        final ValidationResult validation = new ValidationResult();

        if (title().isEmpty()) {
            validation.addError(FIELD_TITLE, "Notification title cannot be empty.");
        }

        try {
            validation.addAll(config().validate());
        } catch (UnsupportedOperationException e) {
            validation.addError(FIELD_CONFIG, "Notification config type cannot be empty.");
        }

        return validation;
    }

    @AutoValue.Builder
    public static abstract class Builder {
        @JsonCreator
        public static Builder create() {
            return new AutoValue_NotificationDto.Builder();
        }

        @Id
        @ObjectId
        @JsonProperty(FIELD_ID)
        public abstract Builder id(String id);

        @JsonProperty(FIELD_TITLE)
        public abstract Builder title(String title);

        @JsonProperty(FIELD_DESCRIPTION)
        public abstract Builder description(String description);

        @JsonProperty(FIELD_CONFIG)
        public abstract Builder config(EventNotificationConfig config);

        public abstract NotificationDto build();
    }

    @Override
    public Object toContentPackEntity(EntityDescriptorIds entityDescriptorIds) {
        final EventNotificationConfigEntity config = config().toContentPackEntity(entityDescriptorIds);
        return NotificationEntity.builder()
            .description(ValueReference.of(description()))
            .title(ValueReference.of(title()))
            .config(config)
            .build();
    }
}
