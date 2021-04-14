/*
 * */
package com.synectiks.process.server.rest.models.alarmcallbacks.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.MoreObjects.firstNonNull;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
@JsonDeserialize(builder = AlertReceivers.Builder.class)
public abstract class AlertReceivers {
    @JsonProperty("emails")
    public abstract List<String> emails();

    @JsonProperty("users")
    public abstract List<String> users();

    public static AlertReceivers create(@Nullable List<String> emails, @Nullable List<String> users) {
        return builder()
                .emails(firstNonNull(emails, Collections.emptyList()))
                .users(firstNonNull(users, Collections.emptyList()))
                .build();
    }

    public static Builder builder() {
        return Builder.create();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonCreator
        public static Builder create() {
            return new AutoValue_AlertReceivers.Builder()
                    .emails(Collections.emptyList())
                    .users(Collections.emptyList());
        }

        @JsonProperty("emails")
        public abstract Builder emails(List<String> emails);

        @JsonProperty("users")
        public abstract Builder users(List<String> users);

        public abstract AlertReceivers build();
    }
}
