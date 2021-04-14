/*
 * */
package com.synectiks.process.server.rest.models.system.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.grok.GrokPattern;

import org.graylog.autovalue.WithBeanGetter;

import java.util.Collection;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class GrokPatternList {
    
    @JsonProperty
    public abstract Collection<GrokPattern> patterns();

    @JsonCreator
    public static GrokPatternList create(@JsonProperty("patterns") Collection<GrokPattern> patternList) {return new AutoValue_GrokPatternList(patternList);}
}
