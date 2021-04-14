/*
 * */
package com.synectiks.process.server.system.stats.elasticsearch;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class NodesStats {
    @JsonProperty
    public abstract int total();

    @JsonProperty
    public abstract int masterOnly();

    @JsonProperty
    public abstract int dataOnly();

    @JsonProperty
    public abstract int masterData();

    @JsonProperty
    public abstract int client();

    public static NodesStats create(int total,
                                   int masterOnly,
                                   int dataOnly,
                                   int masterData,
                                   int client) {
        return new AutoValue_NodesStats(total, masterOnly, dataOnly, masterData, client);
    }
}
