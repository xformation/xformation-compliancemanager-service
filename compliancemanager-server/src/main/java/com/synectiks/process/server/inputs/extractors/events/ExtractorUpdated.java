/*
 * */
package com.synectiks.process.server.inputs.extractors.events;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class ExtractorUpdated {
    @JsonProperty("input_id")
    public abstract String inputId();

    @JsonProperty("extractor_id")
    public abstract String extractorId();

    @JsonCreator
    public static ExtractorUpdated create(@JsonProperty("input_id") String inputId,
                                          @JsonProperty("extractor_id") String extractorId) {
        return new AutoValue_ExtractorUpdated(inputId, extractorId);
    }
}
