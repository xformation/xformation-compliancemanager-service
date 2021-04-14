/*
 * */
package com.synectiks.process.server.rest.models.system.urlwhitelist;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class WhitelistRegexGenerationRequest {
    @NotEmpty
    @JsonProperty("url_template")
    public abstract String urlTemplate();

    @Nullable
    @JsonProperty("placeholder")
    public abstract String placeholder();

    @JsonCreator
    public static WhitelistRegexGenerationRequest create(@JsonProperty("url_template") @NotEmpty String urlTemplate,
            @JsonProperty("placeholder") @Nullable String placeholder) {
        return new AutoValue_WhitelistRegexGenerationRequest(urlTemplate, placeholder);
    }
}
