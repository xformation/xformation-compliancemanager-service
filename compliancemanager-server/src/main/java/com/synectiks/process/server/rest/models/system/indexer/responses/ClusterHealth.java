/*
 * */
package com.synectiks.process.server.rest.models.system.indexer.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class ClusterHealth {
    @JsonProperty
    public abstract String status();

    @JsonProperty
    public abstract ShardStatus shards();

    @JsonCreator
    public static ClusterHealth create(@JsonProperty("status") String status,
                                       @JsonProperty("shards") ShardStatus shards) {
        return new AutoValue_ClusterHealth(status, shards);
    }

    @JsonAutoDetect
    @AutoValue
    @WithBeanGetter
    public static abstract class ShardStatus {
        @JsonProperty
        public abstract int active();

        @JsonProperty
        public abstract int initializing();

        @JsonProperty
        public abstract int relocating();

        @JsonProperty
        public abstract int unassigned();

        @JsonCreator
        public static ShardStatus create(@JsonProperty("active") int active,
                                         @JsonProperty("initializing") int initializing,
                                         @JsonProperty("relocating") int relocating,
                                         @JsonProperty("unassigned") int unassigned) {
            return new AutoValue_ClusterHealth_ShardStatus(active, initializing, relocating, unassigned);
        }
    }
}
