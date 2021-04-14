/*
 * */
package com.synectiks.process.server.rest.models.tools.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class RegexTestRequest {
    @JsonProperty
    public abstract String string();

    @JsonProperty
    public abstract String regex();

    @JsonCreator
    public static RegexTestRequest create(@JsonProperty("string") String string,
                                          @JsonProperty("regex") String regex) {
        return new AutoValue_RegexTestRequest(string, regex);
    }
}
