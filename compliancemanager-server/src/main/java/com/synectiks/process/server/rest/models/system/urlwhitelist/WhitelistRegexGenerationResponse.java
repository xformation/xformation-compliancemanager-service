/*
 * */
package com.synectiks.process.server.rest.models.system.urlwhitelist;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class WhitelistRegexGenerationResponse {
    @JsonProperty("regex")
    public abstract String regex();

    @JsonCreator
    public static WhitelistRegexGenerationResponse create(@JsonProperty("regex") String regex) {
        return new AutoValue_WhitelistRegexGenerationResponse(regex);
    }
}
