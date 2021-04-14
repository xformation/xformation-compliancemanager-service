/*
 * */
package com.synectiks.process.server.rest.models.system.inputs.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.rest.models.system.responses.IOStateSummary;

import org.graylog.autovalue.WithBeanGetter;
import org.joda.time.DateTime;

import javax.annotation.Nullable;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class InputStateSummary extends IOStateSummary {
    @JsonProperty
    public abstract InputSummary messageInput();

    @JsonCreator
    public static InputStateSummary create(@JsonProperty("id") String id,
                                           @JsonProperty("state") String state,
                                           @JsonProperty("started_at") DateTime startedAt,
                                           @JsonProperty("detailed_message") @Nullable String detailedMessage,
                                           @JsonProperty("message_input") InputSummary messageInput) {
        return new AutoValue_InputStateSummary(id, state, startedAt, detailedMessage, messageInput);
    }
}
