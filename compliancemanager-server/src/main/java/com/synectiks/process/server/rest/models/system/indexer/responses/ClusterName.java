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
public abstract class ClusterName {
    @JsonProperty
    public abstract String name();

    @JsonCreator
    public static ClusterName create(@JsonProperty("name") String name) {
        return new AutoValue_ClusterName(name);
    }
}
