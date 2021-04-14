/*
 * */
package com.synectiks.process.server.rest.models.system.grokpattern.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.grok.GrokPattern;

import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class GrokPatternTestRequest {

    @JsonProperty
    public abstract GrokPattern grokPattern();

    @JsonProperty
    public abstract String sampleData();

    @JsonCreator
    public static GrokPatternTestRequest create(@JsonProperty("grok_pattern") GrokPattern grokPattern,
                                         @JsonProperty("sampleData") String sampleData) {
       return new AutoValue_GrokPatternTestRequest(grokPattern, sampleData);
    }
}
