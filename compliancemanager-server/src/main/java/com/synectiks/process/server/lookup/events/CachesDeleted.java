/*
 * */
package com.synectiks.process.server.lookup.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.Set;

@AutoValue
public abstract class CachesDeleted {

    @JsonProperty("ids")
    public abstract Set<String> ids();

    public static CachesDeleted create(String id) {
        return create(Collections.singleton(id));
    }

    @JsonCreator
    public static CachesDeleted create(@JsonProperty("ids") Set<String> ids) {
        return new AutoValue_CachesDeleted(ids);
    }
}
