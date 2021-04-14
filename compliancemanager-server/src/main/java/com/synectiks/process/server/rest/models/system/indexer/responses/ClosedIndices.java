/*
 * */
package com.synectiks.process.server.rest.models.system.indexer.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Set;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class ClosedIndices {
    @JsonProperty
    public abstract Set<String> indices();

    @JsonProperty
    public abstract int total();

    @JsonCreator
    public static ClosedIndices create(@JsonProperty("indices") Set<String> indices, @JsonProperty("total") int total) {
        return new AutoValue_ClosedIndices(indices, total);
    }
}
