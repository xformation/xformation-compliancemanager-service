/*
 * */
package com.synectiks.process.common.events.processor;

import com.google.auto.value.AutoValue;
import com.synectiks.process.common.scheduler.JobDefinitionConfig;
import com.synectiks.process.common.scheduler.JobSchedule;

@AutoValue
public abstract class EventProcessorSchedulerConfig {
    public abstract JobDefinitionConfig jobDefinitionConfig();

    public abstract JobSchedule schedule();

    public static Builder builder() {
        return new AutoValue_EventProcessorSchedulerConfig.Builder();
    }

    public static EventProcessorSchedulerConfig create(JobDefinitionConfig jobDefinitionConfig, JobSchedule schedule) {
        return builder().jobDefinitionConfig(jobDefinitionConfig).schedule(schedule).build();
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder jobDefinitionConfig(JobDefinitionConfig jobDefinitionConfig);

        public abstract Builder schedule(JobSchedule jobSchedule);

        public abstract EventProcessorSchedulerConfig build();
    }
}
