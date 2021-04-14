/*
 * */
package com.synectiks.process.server.rest.models.messages.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.List;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class MessageTokens {
    @JsonProperty
    public abstract List<String> tokens();

    @JsonCreator
    public static MessageTokens create(@JsonProperty("tokens") List<String> tokens) {
        return new AutoValue_MessageTokens(tokens);
    }
}
