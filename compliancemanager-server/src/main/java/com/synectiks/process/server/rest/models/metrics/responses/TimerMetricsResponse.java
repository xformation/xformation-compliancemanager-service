/*
 * */
package com.synectiks.process.server.rest.models.metrics.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
public class TimerMetricsResponse {

    public double min;
    public double max;
    public double mean;

    @JsonProperty("std_dev")
    public double stdDev;

    @JsonProperty("95th_percentile")
    public double percentile95th;

    @JsonProperty("98th_percentile")
    public double percentile98th;

    @JsonProperty("99th_percentile")
    public double percentile99th;

}
