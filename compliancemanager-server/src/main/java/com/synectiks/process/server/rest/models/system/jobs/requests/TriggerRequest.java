/*
 * */
package com.synectiks.process.server.rest.models.system.jobs.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class TriggerRequest {
    @JsonProperty("job_name")
    public abstract String jobName();

    @JsonCreator
    public static TriggerRequest create(@JsonProperty("job_name") String jobName) {
        return new AutoValue_TriggerRequest(jobName);
    }
}
