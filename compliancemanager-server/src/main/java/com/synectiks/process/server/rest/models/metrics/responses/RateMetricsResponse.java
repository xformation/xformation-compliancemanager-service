/*
 * */
package com.synectiks.process.server.rest.models.metrics.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
public class RateMetricsResponse {

    public double total;
    public double mean;

    @JsonProperty("one_minute")
    public double oneMinute;

    @JsonProperty("five_minute")
    public double fiveMinute;

    @JsonProperty("fifteen_minute")
    public double fifteenMinute;

}
