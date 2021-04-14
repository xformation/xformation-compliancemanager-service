/*
 * */
package com.synectiks.process.server.inputs.codecs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.assistedinject.Assisted;
import com.synectiks.process.server.inputs.random.generators.FakeHttpRawMessageGenerator;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.inputs.annotations.Codec;
import com.synectiks.process.server.plugin.inputs.annotations.ConfigClass;
import com.synectiks.process.server.plugin.inputs.annotations.FactoryClass;
import com.synectiks.process.server.plugin.inputs.codecs.AbstractCodec;
import com.synectiks.process.server.plugin.inputs.codecs.CodecAggregator;
import com.synectiks.process.server.plugin.journal.RawMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.synectiks.process.server.inputs.random.generators.FakeHttpRawMessageGenerator.GeneratorState;

import java.io.IOException;

@Codec(name = "random-http-msg", displayName = "Random HTTP Message")
public class RandomHttpMessageCodec extends AbstractCodec {
    private static final Logger log = LoggerFactory.getLogger(RandomHttpMessageCodec.class);
    private final ObjectMapper objectMapper;

    @Inject
    public RandomHttpMessageCodec(@Assisted Configuration configuration, ObjectMapper objectMapper) {
        super(configuration);
        this.objectMapper = objectMapper;
    }

    @Nullable
    @Override
    public Message decode(@Nonnull RawMessage rawMessage) {
        if (!rawMessage.getCodecName().equals(getName())) {
            log.error("Cannot decode payload type {}, skipping message {}",
                      rawMessage.getCodecName(), rawMessage.getId());
            return null;
        }
        try {
            final GeneratorState state = objectMapper.readValue(rawMessage.getPayload(), GeneratorState.class);
            final Message message = FakeHttpRawMessageGenerator.generateMessage(state);
            return message;
        } catch (IOException e) {
            log.error("Cannot decode message to class FakeHttpRawMessageGenerator.GeneratorState", e);
        }
        return null;
    }

    @Nullable
    @Override
    public CodecAggregator getAggregator() {
        return null;
    }


    @FactoryClass
    public interface Factory extends AbstractCodec.Factory<RandomHttpMessageCodec> {
        @Override
        RandomHttpMessageCodec create(Configuration configuration);

        @Override
        Config getConfig();

        @Override
        Descriptor getDescriptor();
    }

    @ConfigClass
    public static class Config extends AbstractCodec.Config {
        @Override
        public void overrideDefaultValues(@Nonnull ConfigurationRequest cr) {

        }
    }

    public static class Descriptor extends AbstractCodec.Descriptor {
        @Inject
        public Descriptor() {
            super(RandomHttpMessageCodec.class.getAnnotation(Codec.class).displayName());
        }
    }
}
