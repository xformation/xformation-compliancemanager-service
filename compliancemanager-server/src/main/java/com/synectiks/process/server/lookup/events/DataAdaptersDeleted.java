/*
 * */
package com.synectiks.process.server.lookup.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.Set;

@AutoValue
public abstract class DataAdaptersDeleted {

    @JsonProperty("ids")
    public abstract Set<String> ids();

    public static DataAdaptersDeleted create(String id) {
        return create(Collections.singleton(id));
    }

    @JsonCreator
    public static DataAdaptersDeleted create(@JsonProperty("ids") Set<String> ids) {
        return new AutoValue_DataAdaptersDeleted(ids);
    }
}
