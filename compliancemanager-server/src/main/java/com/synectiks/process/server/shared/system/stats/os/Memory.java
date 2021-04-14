/*
 * */
package com.synectiks.process.server.shared.system.stats.os;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class Memory {
    @JsonProperty
    public abstract long total();

    @JsonProperty
    public abstract long free();

    @JsonProperty
    public abstract short freePercent();

    @JsonProperty
    public abstract long used();

    @JsonProperty
    public abstract short usedPercent();

    @JsonProperty
    public abstract long actualFree();

    @JsonProperty
    public abstract long actualUsed();

    public static Memory create(long total,
                                long free,
                                short freePercent,
                                long used,
                                short usedPercent,
                                long actualFree,
                                long actualUsed) {
        return new AutoValue_Memory(total, free, freePercent, used, usedPercent, actualFree, actualUsed);
    }
}
