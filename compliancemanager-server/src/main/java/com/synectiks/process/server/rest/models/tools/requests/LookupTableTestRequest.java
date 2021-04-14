/*
 * */
package com.synectiks.process.server.rest.models.tools.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.NotEmpty;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class LookupTableTestRequest {
    @JsonProperty("string")
    @NotEmpty
    public abstract String string();

    @JsonProperty("lookup_table_name")
    @NotEmpty
    public abstract String lookupTableName();

    @JsonCreator
    public static LookupTableTestRequest create(@JsonProperty("string") String string,
                                                @JsonProperty("lookup_table_name") String lookupTableName) {
        return new AutoValue_LookupTableTestRequest(string, lookupTableName);
    }
}
