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
public abstract class TokenList {
    @JsonProperty
    public abstract List<Token> tokens();

    @JsonCreator
    public static TokenList create(@JsonProperty("tokens") List<Token> tokens) {
        return new AutoValue_TokenList(tokens);
    }
}