/*
 * */
package com.synectiks.process.server.rest.models.metrics.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
public class TimerRateMetricsResponse {

    public TimerMetricsResponse time;
    public RateMetricsResponse rate;

    @JsonProperty("duration_unit")
    public String durationUnit;

    @JsonProperty("rate_unit")
    public String rateUnit;

}
