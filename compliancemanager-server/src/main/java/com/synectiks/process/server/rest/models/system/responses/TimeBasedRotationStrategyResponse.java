/*
 * */
package com.synectiks.process.server.rest.models.system.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;
import org.joda.time.Period;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class TimeBasedRotationStrategyResponse implements DeflectorConfigResponse {
    @JsonProperty("max_time_per_index")
    public abstract Period maxTimePerIndex();

    public static TimeBasedRotationStrategyResponse create(@JsonProperty(TYPE_FIELD) String type,
                                                           @JsonProperty("max_number_of_indices") int maxNumberOfIndices,
                                                           @JsonProperty("max_time_per_index") Period maxTimePerIndex) {
        return new AutoValue_TimeBasedRotationStrategyResponse(type, maxNumberOfIndices, maxTimePerIndex);
    }

    public static TimeBasedRotationStrategyResponse create(@JsonProperty("max_number_of_indices") int maxNumberOfIndices,
                                                           @JsonProperty("max_time_per_index") Period maxTimePerIndex) {
        return create(TimeBasedRotationStrategyResponse.class.getCanonicalName(), maxNumberOfIndices, maxTimePerIndex);
    }
}
