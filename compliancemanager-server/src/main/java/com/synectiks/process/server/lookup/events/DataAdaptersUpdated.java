/*
 * */
package com.synectiks.process.server.lookup.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.Set;

@AutoValue
public abstract class DataAdaptersUpdated {

    @JsonProperty("ids")
    public abstract Set<String> ids();

    public static DataAdaptersUpdated create(String id) {
        return create(Collections.singleton(id));
    }

    @JsonCreator
    public static DataAdaptersUpdated create(@JsonProperty("ids") Set<String> ids) {
        return new AutoValue_DataAdaptersUpdated(ids);
    }
}
