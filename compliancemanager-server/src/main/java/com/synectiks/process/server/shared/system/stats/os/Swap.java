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
public abstract class Swap {
    @JsonProperty
    public abstract long total();

    @JsonProperty
    public abstract long free();

    @JsonProperty
    public abstract long used();

    public static Swap create(long total,
                              long free,
                              long used) {
        return new AutoValue_Swap(total, free, used);
    }
}
