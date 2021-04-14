/*
 * */
package com.synectiks.process.common.events.contentpack.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synectiks.process.common.events.processor.EventProcessorConfig;
import com.synectiks.process.server.contentpacks.NativeEntityConverter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = EventProcessorConfigEntity.TYPE_FIELD,
        visible = true)
public interface EventProcessorConfigEntity extends NativeEntityConverter<EventProcessorConfig> {
    String TYPE_FIELD = "type";

    @JsonProperty(TYPE_FIELD)
    String type();

    interface Builder<SELF> {
        @JsonProperty(TYPE_FIELD)
        SELF type(String type);
    }
}
