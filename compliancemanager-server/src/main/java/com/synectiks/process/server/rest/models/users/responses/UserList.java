/*
 * */
package com.synectiks.process.server.rest.models.users.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.List;

@JsonAutoDetect

@AutoValue
@WithBeanGetter
public abstract class UserList {
    @JsonProperty
    public abstract List<UserSummary> users();

    @JsonCreator
    public static UserList create(@JsonProperty("users") List<UserSummary> users) {
        return new AutoValue_UserList(users);
    }
}
