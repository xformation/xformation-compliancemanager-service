/*
 * */
package com.synectiks.process.server.system.urlwhitelist;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({@Type(value = LiteralWhitelistEntry.class, name = "literal"),
        @Type(value = RegexWhitelistEntry.class, name = "regex")})
public interface WhitelistEntry {
    enum Type {
        @JsonProperty("literal")
        LITERAL,
        @JsonProperty("regex")
        REGEX
    }

    @JsonProperty("id")
    String id();

    @JsonProperty("type")
    Type type();

    @JsonProperty("title")
    String title();

    @JsonProperty("value")
    String value();

    boolean isWhitelisted(String url);
}
