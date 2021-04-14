/*
 * */
package com.synectiks.process.server.rest.models.system.indexer.responses;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class FailureCount {
    @JsonProperty
    public abstract long count();

    @JsonCreator
    public static FailureCount create(@JsonProperty("count") long count) {
        return new AutoValue_FailureCount(count);
    }
}
