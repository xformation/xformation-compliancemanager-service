/*
 * */
package com.synectiks.process.server.rest.models.system.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class SizeBasedRotationStrategyResponse implements DeflectorConfigResponse {
    @JsonProperty("max_size_per_index")
    public abstract long maxSizePerIndex();

    public static SizeBasedRotationStrategyResponse create(@JsonProperty(TYPE_FIELD) String type,
                                                           @JsonProperty("max_number_of_indices") int maxNumberOfIndices,
                                                           @JsonProperty("max_size_per_index") long maxSizePerIndex) {
        return new AutoValue_SizeBasedRotationStrategyResponse(type, maxNumberOfIndices, maxSizePerIndex);
    }

    public static SizeBasedRotationStrategyResponse create(@JsonProperty("max_number_of_indices") int maxNumberOfIndices,
                                                           @JsonProperty("max_size_per_index") long maxSizePerIndex) {
        return create(SizeBasedRotationStrategyResponse.class.getCanonicalName(), maxNumberOfIndices, maxSizePerIndex);
    }
}
