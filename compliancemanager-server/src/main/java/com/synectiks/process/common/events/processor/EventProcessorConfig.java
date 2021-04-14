/*
 * */
package com.synectiks.process.common.events.processor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synectiks.process.common.events.contentpack.entities.EventProcessorConfigEntity;
import com.synectiks.process.common.scheduler.JobDefinitionConfig;
import com.synectiks.process.common.scheduler.clock.JobSchedulerClock;
import com.synectiks.process.server.contentpacks.ContentPackable;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.plugin.rest.ValidationResult;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = EventProcessorConfig.TYPE_FIELD,
        visible = true,
        defaultImpl = EventProcessorConfig.FallbackConfig.class)
public interface EventProcessorConfig extends ContentPackable<EventProcessorConfigEntity> {
    String TYPE_FIELD = "type";

    @JsonProperty(TYPE_FIELD)
    String type();

    /**
     * Returns a {@link JobDefinitionConfig} for this event processor configuration. If the event processor shouldn't
     * be scheduled, this method returns an empty {@link Optional}.
     *
     * @param eventDefinition the event definition
     * @param clock           the clock that can be used to get the current time
     * @return the job definition config or an empty optional if the processor shouldn't be scheduled
     */
    @JsonIgnore
    default Optional<EventProcessorSchedulerConfig> toJobSchedulerConfig(EventDefinition eventDefinition, JobSchedulerClock clock) {
        return Optional.empty();
    }

    /**
     * Validates the event processor configuration.
     *
     * @return the validation result
     */
    @JsonIgnore
    ValidationResult validate();

    /**
     * Returns the permissions that are required to create the event processor configuration. (e.g. stream permissions)
     *
     * @return the required permissions
     */
    @JsonIgnore
    default Set<String> requiredPermissions() {
        return Collections.emptySet();
    }

    interface Builder<SELF> {
        @JsonProperty(TYPE_FIELD)
        SELF type(String type);
    }

    class FallbackConfig implements EventProcessorConfig {
        @Override
        public String type() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ValidationResult validate() {
            throw new UnsupportedOperationException();
        }

        @Override
        public EventProcessorConfigEntity toContentPackEntity(EntityDescriptorIds entityDescriptorIds) {
            return null;
        }
    }
}
