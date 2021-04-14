/*
 * */
package com.synectiks.process.server.rest.models.users.requests;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class Startpage {
    @JsonProperty
    @Nullable
    public abstract String type();

    @JsonProperty
    @Nullable
    public abstract String id();

    @JsonCreator
    public static Startpage create(@JsonProperty("type") @Nullable String type,
                                   @JsonProperty("id") @Nullable String id) {
        return new AutoValue_Startpage(type, id);
    }
}