/*
 * */
package com.synectiks.process.server.rest.resources.streams.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@AutoValue
public abstract class AlertConditionTestResponse {

    @JsonProperty("triggered")
    public abstract boolean triggered();

    @JsonProperty("description")
    public abstract Optional<String> description();

    @JsonProperty("error")
    public boolean error() {
        return !errorMessages().isEmpty();
    }

    @JsonProperty("error_messages")
    public abstract ImmutableList<ErrorMessage> errorMessages();

    public static AlertConditionTestResponse create(boolean triggered, @Nullable String description) {
        return createResult(triggered, description, Collections.emptyList());
    }

    public static AlertConditionTestResponse createWithError(Throwable t) {
        return createResult(false, null, ImmutableList.of(ErrorMessage.create(t)));
    }

    private static AlertConditionTestResponse createResult(boolean triggered, @Nullable String description, Collection<ErrorMessage> errorMessages) {
        return new AutoValue_AlertConditionTestResponse(triggered, Optional.ofNullable(description), ImmutableList.copyOf(errorMessages));
    }
    @AutoValue
    public static abstract class ErrorMessage {

        @JsonProperty("type")
        public abstract String type();

        @JsonProperty("message")
        @Nullable
        public abstract String message();
        public static ErrorMessage create(Throwable t) {
            return new AutoValue_AlertConditionTestResponse_ErrorMessage(t.getClass().getCanonicalName(), t.getMessage());
        }
    }

}
