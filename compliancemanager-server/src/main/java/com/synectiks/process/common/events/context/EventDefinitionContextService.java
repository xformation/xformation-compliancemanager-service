/*
 * */
package com.synectiks.process.common.events.context;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.synectiks.process.common.events.processor.EventDefinitionDto;
import com.synectiks.process.common.events.processor.EventProcessorExecutionJob;
import com.synectiks.process.common.scheduler.DBJobDefinitionService;
import com.synectiks.process.common.scheduler.DBJobTriggerService;
import com.synectiks.process.common.scheduler.JobDefinitionDto;
import com.synectiks.process.common.scheduler.JobTriggerData;
import com.synectiks.process.common.scheduler.JobTriggerDto;
import com.synectiks.process.common.scheduler.JobTriggerStatus;

import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Collects additional information for {@link com.synectiks.process.common.events.processor.EventDefinition event definitions} like
 * scheduler information. This allows us to return additional information for event definitions without modifying
 * their DTOs.
 */
public class EventDefinitionContextService {
    private final DBJobDefinitionService jobDefinitionService;
    private final DBJobTriggerService jobTriggerService;

    @Inject
    public EventDefinitionContextService(DBJobDefinitionService jobDefinitionService, DBJobTriggerService jobTriggerService) {
        this.jobDefinitionService = jobDefinitionService;
        this.jobTriggerService = jobTriggerService;
    }

    public ImmutableMap<String, Object> contextFor(List<EventDefinitionDto> eventDefinitions) {
        return ImmutableMap.of("scheduler", schedulerContext(eventDefinitions));
    }

    public ImmutableMap<String, Object> contextFor(EventDefinitionDto eventDefinition) {
        final ImmutableMap<String, SchedulerCtx> schedulerContext = schedulerContext(Collections.singletonList(eventDefinition));
        return ImmutableMap.of("scheduler", schedulerContext.get(eventDefinition.id()));
    }

    private Map<String, List<JobDefinitionDto>> getJobDefinitions(List<EventDefinitionDto> eventDefinitions) {
        final Set<String> eventDefinitionIds = eventDefinitions.stream().map(EventDefinitionDto::id).collect(Collectors.toSet());
        return jobDefinitionService.getAllByConfigField(EventProcessorExecutionJob.Config.FIELD_EVENT_DEFINITION_ID, eventDefinitionIds);
    }

    private Map<String, List<JobTriggerDto>> getJobTriggers(Map<String, List<JobDefinitionDto>> jobDefinitions) {
        final Set<String> jobDefinitionIds = jobDefinitions.values().stream()
                .flatMap(Collection::stream)
                .map(JobDefinitionDto::id)
                .collect(Collectors.toSet());
        return jobTriggerService.getForJobs(jobDefinitionIds);
    }

    private ImmutableMap<String, SchedulerCtx> schedulerContext(List<EventDefinitionDto> eventDefinitions) {
        // We try to minimize database queries by fetching all required job definitions and triggers in two requests
        // TODO: Use MongoDB's $lookup aggregation operator once we switch to MongoDB 4.0 to do this with a single database query
        final Map<String, List<JobDefinitionDto>> jobDefinitions = getJobDefinitions(eventDefinitions);
        final Map<String, List<JobTriggerDto>> jobTriggers = getJobTriggers(jobDefinitions);

        final ImmutableMap.Builder<String, SchedulerCtx> ctx = ImmutableMap.builder();

        for (final EventDefinitionDto eventDefinition : eventDefinitions) {
            if (eventDefinition.id() == null) {
                // Should not happen!
                throw new IllegalStateException("Event definition doesn't have an ID: " + eventDefinition);
            }
            if (!jobDefinitions.containsKey(eventDefinition.id())) {
                ctx.put(eventDefinition.id(), SchedulerCtx.unscheduled());
                continue;
            }
            if (jobDefinitions.get(eventDefinition.id()).size() > 1) {
                throw new IllegalStateException("Cannot handle multiple job definitions for a single event definition");
            }

            final JobDefinitionDto jobDefinition = jobDefinitions.get(eventDefinition.id()).get(0);

            // DBJobTriggerService#getForJobs currently returns only one trigger. (raises an exception otherwise)
            // Once we allow multiple triggers per job definition, this code will fail.
            // TODO: Fix this code for multiple triggers per job definition
            final JobTriggerDto trigger = jobTriggers.get(jobDefinition.id()).get(0);

            if (trigger != null) {
                ctx.put(eventDefinition.id(), SchedulerCtx.scheduled(trigger));
            }
        }

        return ctx.build();
    }

    @AutoValue
    public static abstract class SchedulerCtx {
        @JsonProperty("is_scheduled")
        public abstract boolean isScheduled();

        @JsonProperty("status")
        public abstract Optional<JobTriggerStatus> status();

        @JsonProperty("next_time")
        public abstract Optional<DateTime> nextTime();

        @JsonProperty("triggered_at")
        public abstract Optional<DateTime> triggeredAt();

        @JsonProperty("data")
        public abstract Optional<JobTriggerData> data();

        public static SchedulerCtx unscheduled() {
            return create(false, null);
        }

        public static SchedulerCtx scheduled(JobTriggerDto trigger) {
            return create(true, trigger);
        }

        private static SchedulerCtx create(boolean isScheduled, JobTriggerDto trigger) {
            final Optional<JobTriggerDto> optionalTrigger = Optional.ofNullable(trigger);

            return new AutoValue_EventDefinitionContextService_SchedulerCtx(
                    isScheduled,
                    optionalTrigger.map(JobTriggerDto::status),
                    optionalTrigger.map(JobTriggerDto::nextTime),
                    optionalTrigger.flatMap(JobTriggerDto::triggeredAt),
                    optionalTrigger.flatMap(JobTriggerDto::data)
            );
        }
    }
}
