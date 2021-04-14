/*
 * */
package com.synectiks.process.server.users.events;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class UserDeletedEvent {
    @JsonProperty("user_id")
    public abstract String userId();

    @JsonProperty("user_name")
    public abstract String userName();

    @JsonCreator
    public static UserDeletedEvent create(@JsonProperty("user_id") String userId,
                                          @JsonProperty("user_name") String userName) {
        return new AutoValue_UserDeletedEvent(userId, userName);
    }
}
