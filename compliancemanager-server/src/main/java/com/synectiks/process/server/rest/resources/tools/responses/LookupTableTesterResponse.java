/*
 * */
package com.synectiks.process.server.rest.resources.tools.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.lookup.LookupResult;

import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class LookupTableTesterResponse {
    @JsonProperty("empty")
    public abstract boolean empty();

    @JsonProperty("error")
    public abstract boolean error();

    @JsonProperty("error_message")
    public abstract String errorMessage();

    @JsonProperty("key")
    @Nullable
    public abstract Object key();

    @JsonProperty("value")
    @Nullable
    public abstract Object value();

    @JsonCreator
    public static LookupTableTesterResponse create(@JsonProperty("empty") boolean empty,
                                                   @JsonProperty("error") boolean error,
                                                   @JsonProperty("error_message") String errorMessage,
                                                   @JsonProperty("key") @Nullable Object key,
                                                   @JsonProperty("value") @Nullable Object value) {
        return new AutoValue_LookupTableTesterResponse(empty, error, errorMessage, key, value);
    }

    public static LookupTableTesterResponse error(String errorMessage) {
        return create(true, true, errorMessage, null, null);
    }

    public static LookupTableTesterResponse emptyResult(String string) {
        return create(true, false, "", string, null);
    }

    public static LookupTableTesterResponse result(String string, LookupResult result) {
        return create(result.isEmpty(), false, "", string, result.singleValue());
    }
}
