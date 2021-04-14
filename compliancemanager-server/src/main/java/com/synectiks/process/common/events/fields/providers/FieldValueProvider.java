/*
 * */
package com.synectiks.process.common.events.fields.providers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synectiks.process.common.events.event.EventWithContext;
import com.synectiks.process.common.events.fields.FieldValue;

public interface FieldValueProvider {
    interface Factory<TYPE extends FieldValueProvider> {
        TYPE create(Config config);
    }

    FieldValue get(String fieldName, EventWithContext eventWithContext);

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = Config.TYPE_FIELD,
            visible = true,
            defaultImpl = Config.FallbackActionConfig.class)
    interface Config {
        String TYPE_FIELD = "type";

        @JsonProperty(TYPE_FIELD)
        String type();

        interface Builder<SELF> {
            @JsonProperty(TYPE_FIELD)
            SELF type(String type);
        }

        class FallbackActionConfig implements Config {
            @Override
            public String type() {
                throw new UnsupportedOperationException();
            }
        }
    }
}
