/*
 * */
package com.synectiks.process.server.rest.models.system.indexer.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.List;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class IndicesReadRequest {
    @JsonProperty("indices")
    public abstract List<String> indices();

    @JsonCreator
    public static IndicesReadRequest create(@JsonProperty("indices") List<String> indices) {
        return new AutoValue_IndicesReadRequest(indices);
    }
}
