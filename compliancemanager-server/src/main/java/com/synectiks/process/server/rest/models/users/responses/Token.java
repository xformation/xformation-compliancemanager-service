/*
 * */
package com.synectiks.process.server.rest.models.users.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;
import org.joda.time.DateTime;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class Token {
    @JsonProperty
    public abstract String id();

    @JsonProperty
    public abstract String name();

    @JsonProperty
    public abstract String token();

    @JsonProperty
    public abstract DateTime lastAccess();

    @JsonCreator
    public static Token create(@JsonProperty("id") String id,
                               @JsonProperty("name") String name,
                               @JsonProperty("token") String token,
                               @JsonProperty("last_access") DateTime lastAccess) {
        return new AutoValue_Token(id, name, token, lastAccess);
    }
}
