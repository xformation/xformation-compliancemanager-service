/*
 * */
package com.synectiks.process.common.events.notifications;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synectiks.process.common.events.contentpack.entities.EventNotificationConfigEntity;
import com.synectiks.process.common.events.event.EventDto;
import com.synectiks.process.common.scheduler.JobTriggerData;
import com.synectiks.process.server.contentpacks.ContentPackable;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.plugin.rest.ValidationResult;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = EventNotificationConfig.TYPE_FIELD,
        visible = true,
        defaultImpl = EventNotificationConfig.FallbackNotificationConfig.class)
public interface EventNotificationConfig extends ContentPackable<EventNotificationConfigEntity> {
    String FIELD_NOTIFICATION_ID = "notification_id";
    String TYPE_FIELD = "type";

    @JsonProperty(TYPE_FIELD)
    String type();

    interface Builder<SELF> {
        @JsonProperty(TYPE_FIELD)
        SELF type(String type);
    }

    @JsonIgnore
    JobTriggerData toJobTriggerData(EventDto dto);

    @JsonIgnore
    ValidationResult validate();

    class FallbackNotificationConfig implements EventNotificationConfig {
        @Override
        public String type() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ValidationResult validate() {
            throw new UnsupportedOperationException();
        }

        @Override
        public JobTriggerData toJobTriggerData(EventDto dto) {
            return null;
        }

        @Override
        public EventNotificationConfigEntity toContentPackEntity(EntityDescriptorIds entityDescriptorIds) {
            return null;
        }
    }
}
