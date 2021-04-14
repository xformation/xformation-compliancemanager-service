/*
 * */
package com.synectiks.process.server.indexer.indexset.events;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;

import org.graylog.autovalue.WithBeanGetter;

import javax.validation.Valid;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class IndexSetCreatedEvent {
    @JsonProperty("index_set")
    public abstract IndexSetConfig indexSet();

    @JsonCreator
    public static IndexSetCreatedEvent create(@JsonProperty("index_set") @Valid IndexSetConfig indexSet) {
        return new AutoValue_IndexSetCreatedEvent(indexSet);
    }
}
