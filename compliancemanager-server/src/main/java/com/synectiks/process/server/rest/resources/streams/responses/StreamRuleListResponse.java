/*
 * */
package com.synectiks.process.server.rest.resources.streams.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.streams.StreamRule;

import org.graylog.autovalue.WithBeanGetter;

import java.util.Collection;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class StreamRuleListResponse {
    @JsonProperty
    public abstract int total();

    @JsonProperty(value = "stream_rules")
    public abstract Collection<StreamRule> streamRules();

    public static StreamRuleListResponse create(int total, Collection<StreamRule> streamRules) {
        return new AutoValue_StreamRuleListResponse(total, streamRules);
    }
}
