/*
 * */
package com.synectiks.process.server.rest.models.system.indexer.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Map;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class OpenIndicesInfo {
    @JsonProperty
    public abstract Map<String, IndexInfo> indices();

    @JsonCreator
    public static OpenIndicesInfo create(@JsonProperty("indices") Map<String, IndexInfo> indices) {
        return new AutoValue_OpenIndicesInfo(indices);
    }
}
