/*
 * */
package com.synectiks.process.server.rest.models.tools.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@JsonAutoDetect
@AutoValue
public abstract class ContainsStringTestRequest {
    @JsonProperty("search_string")
    public abstract String searchString();

    @JsonProperty("string")
    public abstract String string();

    @JsonCreator
    public static ContainsStringTestRequest create(@JsonProperty("search_string") String searchString,
                                                   @JsonProperty("string") String string) {
        return new AutoValue_ContainsStringTestRequest(searchString, string);
    }
}
