/*
 * */
package com.synectiks.process.common.security;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Locale;

public enum Capability {
    @JsonProperty("view")
    VIEW(1),
    @JsonProperty("manage")
    MANAGE(2),
    @JsonProperty("own")
    OWN(3);

    private final int priority;

    public int priority() {
        return priority;
    }

    Capability(int priority) {
        this.priority = priority;
    }

    public String toId() {
        return name().toLowerCase(Locale.US);
    }
}
