/*
 * */
package com.synectiks.process.server.rest.resources.search.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.rest.DetailedError;
import com.synectiks.process.server.plugin.rest.GenericError;

import javax.annotation.Nullable;
import java.util.Collection;

@JsonAutoDetect
@AutoValue
public abstract class QueryParseError implements DetailedError {
    @JsonProperty
    @Nullable
    public abstract Integer line();

    @JsonProperty
    @Nullable
    public abstract Integer column();


    public static QueryParseError create(String message,
                                         Collection<String> details,
                                         @Nullable Integer line,
                                         @Nullable Integer column) {
        return new AutoValue_QueryParseError(message, details, line, column);
    }
}
