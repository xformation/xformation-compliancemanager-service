/*
 * */
package com.synectiks.process.server.messageprocessors;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.messageprocessors.MessageProcessor;

import org.graylog.autovalue.WithBeanGetter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class MessageProcessorsConfigWithDescriptors {
    @JsonProperty("processor_order")
    public abstract List<MessageProcessorDescriptor> processorOrder();

    @JsonProperty("disabled_processors")
    public abstract Set<String> disabledProcessors();

    @JsonCreator
    public static MessageProcessorsConfigWithDescriptors create(@JsonProperty("processor_order") List<MessageProcessorDescriptor> processorOrder,
                                                                @JsonProperty("disabled_processors") Set<String> disabledProcessors) {
        return builder()
                .processorOrder(processorOrder)
                .disabledProcessors(disabledProcessors)
                .build();
    }

    public static MessageProcessorsConfigWithDescriptors fromConfig(MessageProcessorsConfig config,
                                                                    Set<MessageProcessor.Descriptor> descriptors) {
        final Map<String, MessageProcessor.Descriptor> descriptorMap = descriptors.stream()
                .collect(Collectors.toMap(MessageProcessor.Descriptor::className, descriptor -> descriptor));

        return builder()
                .processorOrder(config.processorOrder().stream()
                        .map(s -> MessageProcessorDescriptor.fromDescriptor(descriptorMap.get(s)))
                        .collect(Collectors.toList()))
                .disabledProcessors(config.disabledProcessors())
                .build();
    }

    public MessageProcessorsConfig toConfig() {
        return MessageProcessorsConfig.builder()
                .processorOrder(processorOrder().stream()
                        .map(MessageProcessorDescriptor::className)
                        .collect(Collectors.toList()))
                .disabledProcessors(disabledProcessors())
                .build();
    }

    public static Builder builder() {
        return new AutoValue_MessageProcessorsConfigWithDescriptors.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder processorOrder(List<MessageProcessorDescriptor> processorOrder);

        public abstract Builder disabledProcessors(Set<String> disabledMessageProcessors);

        public abstract MessageProcessorsConfigWithDescriptors build();
    }
}