/*
 * */
package com.synectiks.process.server.indexer.indexset.events;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.NotBlank;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class IndexSetDeletedEvent {
    @JsonProperty("id")
    @NotBlank
    public abstract String id();

    @JsonCreator
    public static IndexSetDeletedEvent create(@JsonProperty("id") @NotBlank String id) {
        return new AutoValue_IndexSetDeletedEvent(id);
    }
}
