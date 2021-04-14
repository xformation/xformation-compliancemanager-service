/*
 * */
package com.synectiks.process.server.rest.models.system.inputs.extractors.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Map;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class OrderExtractorsRequest {
    @JsonProperty
    public abstract Map<Integer, String> order();

    @JsonCreator
    public static OrderExtractorsRequest create(@JsonProperty("order") Map<Integer, String> order) {
        return new AutoValue_OrderExtractorsRequest(order);
    }
}
