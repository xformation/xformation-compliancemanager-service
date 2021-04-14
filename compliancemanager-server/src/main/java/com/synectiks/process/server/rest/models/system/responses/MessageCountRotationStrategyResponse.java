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
public abstract class MessageCountRotationStrategyResponse implements DeflectorConfigResponse {
    @JsonProperty("max_docs_per_index")
    public abstract int maxDocsPerIndex();

    public static MessageCountRotationStrategyResponse create(@JsonProperty(TYPE_FIELD) String type,
                                                              @JsonProperty("max_number_of_indices") int maxNumberOfIndices,
                                                              @JsonProperty("max_docs_per_index") int maxDocsPerIndex) {
        return new AutoValue_MessageCountRotationStrategyResponse(type, maxNumberOfIndices, maxDocsPerIndex);
    }

    public static MessageCountRotationStrategyResponse create(@JsonProperty("max_number_of_indices") int maxNumberOfIndices,
                                                              @JsonProperty("max_docs_per_index") int maxDocsPerIndex) {
        return create(MessageCountRotationStrategyResponse.class.getCanonicalName(), maxNumberOfIndices, maxDocsPerIndex);
    }
}
