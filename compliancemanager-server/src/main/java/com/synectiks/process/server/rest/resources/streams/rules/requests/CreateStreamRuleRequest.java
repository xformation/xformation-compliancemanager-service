/*
 * */
package com.synectiks.process.server.rest.resources.streams.rules.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class CreateStreamRuleRequest {
    @JsonProperty
    public abstract int type();

    @JsonProperty
    public abstract String value();

    @JsonProperty
    public abstract String field();

    @JsonProperty
    public abstract boolean inverted();

    @JsonProperty
    @Nullable
    public abstract String description();

    @JsonCreator
    public static CreateStreamRuleRequest create(@JsonProperty("type") int type,
                                                 @JsonProperty("value") String value,
                                                 @JsonProperty("field") String field,
                                                 @JsonProperty("inverted") boolean inverted,
                                                 @JsonProperty("description") @Nullable String description) {
        return new AutoValue_CreateStreamRuleRequest(type, value, field, inverted, description);
    }
}
