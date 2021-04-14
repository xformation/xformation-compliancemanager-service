/*
 * */
package com.synectiks.process.common.events.contentpack.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synectiks.process.common.events.notifications.EventNotificationConfig;
import com.synectiks.process.server.contentpacks.NativeEntityConverter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = com.synectiks.process.common.events.notifications.EventNotificationConfig.TYPE_FIELD,
        visible = true)
public interface EventNotificationConfigEntity extends NativeEntityConverter<EventNotificationConfig> {
    String TYPE_FIELD = "type";

    @JsonProperty(TYPE_FIELD)
    String type();

    interface Builder<SELF> {
        @JsonProperty(TYPE_FIELD)
        SELF type(String type);
    }
}



