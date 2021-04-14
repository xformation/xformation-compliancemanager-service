/*
 * */
package com.synectiks.process.server.inputs.codecs;

import com.google.inject.Inject;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.codecs.Codec;

import java.util.Map;

public class CodecFactory {
    private Map<String, Codec.Factory<? extends Codec>> codecFactory;

    @Inject
    public CodecFactory(Map<String, Codec.Factory<? extends Codec>> codecFactory) {
        this.codecFactory = codecFactory;
    }

    public Map<String, Codec.Factory<? extends Codec>> getFactory() {
        return codecFactory;
    }

    public Codec create(String type, Configuration configuration) {
        final Codec.Factory<? extends Codec> factory = this.codecFactory.get(type);

        if (factory == null) {
            throw new IllegalArgumentException("Codec type " + type + " does not exist.");
        }

        return factory.create(configuration);
    }
}
