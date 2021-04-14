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
public abstract class WhitelistCheckResponse {
    @JsonProperty("url")
    public abstract String url();

    @JsonProperty("is_whitelisted")
    public abstract boolean isWhitelisted();

    @JsonCreator
    public static WhitelistCheckResponse create(@JsonProperty("url") String url,
                                                @JsonProperty("is_whitelisted") boolean isWhitelisted) {
        return new AutoValue_WhitelistCheckResponse(url, isWhitelisted);
    }
}
