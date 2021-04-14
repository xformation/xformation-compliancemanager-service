/*
 * */
package com.synectiks.process.server.rest.resources.streams.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class SingleStreamRuleSummaryResponse {
    @JsonProperty("streamrule_id")
    public abstract String streamRuleId();

    public static SingleStreamRuleSummaryResponse create(String streamRuleId) {
        return new AutoValue_SingleStreamRuleSummaryResponse(streamRuleId);
    }
}
