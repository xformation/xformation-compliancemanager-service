/*
 * */
package com.synectiks.process.server.rest.models.system.indexer.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class AllIndices {
    @JsonProperty("closed")
    public abstract ClosedIndices closed();

    @JsonProperty("reopened")
    public abstract ClosedIndices reopened();

    @JsonProperty("all")
    public abstract OpenIndicesInfo all();

    @JsonCreator
    public static AllIndices create(@JsonProperty("closed") ClosedIndices closed,
                                    @JsonProperty("reopened") ClosedIndices reopened,
                                    @JsonProperty("all") OpenIndicesInfo all) {
        return new AutoValue_AllIndices(closed, reopened, all);
    }
}
