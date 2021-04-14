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
public abstract class OsStats {
    public static final double[] EMPTY_LOAD = new double[0];

    @JsonProperty
    @SuppressWarnings("mutable")
    public abstract double[] loadAverage();

    @JsonProperty
    public abstract long uptime();

    @JsonProperty
    public abstract Processor processor();

    @JsonProperty
    public abstract Memory memory();

    @JsonProperty
    public abstract Swap swap();

    public static OsStats create(double[] loadAverage,
                                 long uptime,
                                 Processor processor,
                                 Memory memory,
                                 Swap swap) {
        return new AutoValue_OsStats(loadAverage, uptime, processor, memory, swap);
    }
}
