/*
 * */
package com.synectiks.process.server.grok;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.Set;

@JsonAutoDetect
@AutoValue
public abstract class GrokPatternsDeletedEvent {
    @JsonProperty("patterns")
    public abstract Set<String> patterns();

    @JsonCreator
    public static GrokPatternsDeletedEvent create(@JsonProperty("patterns") Set<String> patterns) {
        return new AutoValue_GrokPatternsDeletedEvent(patterns);
    }
}
