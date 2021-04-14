/*
 * */
package com.synectiks.process.server.rest.models.tools.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class RegexReplaceTestRequest {
    @JsonProperty
    @NotNull
    public abstract String string();

    @JsonProperty
    @NotEmpty
    public abstract String regex();

    @JsonProperty
    @NotNull
    public abstract String replacement();

    @JsonProperty("replace_all")
    public abstract boolean replaceAll();

    @JsonCreator
    public static RegexReplaceTestRequest create(@JsonProperty("string") @NotNull String string,
                                                 @JsonProperty("regex") @NotEmpty String regex,
                                                 @JsonProperty("replacement") @NotNull String replacement,
                                                 @JsonProperty("replace_all") boolean replaceAll) {
        return new AutoValue_RegexReplaceTestRequest(string, regex, replacement, replaceAll);
    }
}
