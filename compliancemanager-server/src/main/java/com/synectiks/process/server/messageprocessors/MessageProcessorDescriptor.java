/*
 * */
package com.synectiks.process.server.messageprocessors;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.messageprocessors.MessageProcessor;

import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class MessageProcessorDescriptor {
    @JsonProperty("name")
    public abstract String name();

    @JsonProperty("class_name")
    public abstract String className();

    @JsonCreator
    public static MessageProcessorDescriptor create(@JsonProperty("name") String name,
                                                    @JsonProperty("class_name") String className) {
        return new AutoValue_MessageProcessorDescriptor(name, className);
    }

    public static MessageProcessorDescriptor fromDescriptor(MessageProcessor.Descriptor descriptor) {
        return create(descriptor.name(), descriptor.className());
    }
}