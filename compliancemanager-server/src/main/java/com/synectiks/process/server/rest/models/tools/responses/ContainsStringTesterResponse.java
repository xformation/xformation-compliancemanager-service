/*
 * */
package com.synectiks.process.server.rest.models.tools.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;

@JsonAutoDetect
@AutoValue
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ContainsStringTesterResponse {
    @JsonProperty("matched")
    public abstract boolean matched();

    @JsonProperty("match")
    @Nullable
    public abstract Match match();

    @JsonProperty("search_string")
    public abstract String searchString();

    @JsonProperty("string")
    public abstract String string();

    @JsonCreator
    public static ContainsStringTesterResponse create(@JsonProperty("matched") boolean matched,
                                                      @JsonProperty("match") @Nullable Match match,
                                                      @JsonProperty("search_string") String searchString,
                                                      @JsonProperty("string") String string) {
        return new AutoValue_ContainsStringTesterResponse(matched, match, searchString, string);
    }

    @JsonAutoDetect
    @AutoValue
    public static abstract class Match {
        @JsonProperty
        public abstract int start();

        @JsonProperty
        public abstract int end();

        @JsonCreator
        public static Match create(@JsonProperty("start") int start,
                                   @JsonProperty("end") int end) {
            return new AutoValue_ContainsStringTesterResponse_Match(start, end);
        }
    }
}
