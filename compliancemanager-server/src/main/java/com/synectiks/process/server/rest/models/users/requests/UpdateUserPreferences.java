/*
 * */
package com.synectiks.process.server.rest.models.users.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Map;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class UpdateUserPreferences {

    @JsonProperty
    public abstract Map<String, Object> preferences();

    @JsonCreator
    public static UpdateUserPreferences create(@JsonProperty("preferences") Map<String, Object> preferences) {
        return new AutoValue_UpdateUserPreferences(preferences);
    }
}
