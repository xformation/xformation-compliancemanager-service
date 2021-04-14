/*
 * */
package com.synectiks.process.server.inputs.codecs;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.jayway.jsonpath.JsonPath;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.configuration.fields.ConfigurationField;
import com.synectiks.process.server.plugin.configuration.fields.TextField;
import com.synectiks.process.server.plugin.inputs.annotations.Codec;
import com.synectiks.process.server.plugin.inputs.annotations.ConfigClass;
import com.synectiks.process.server.plugin.inputs.annotations.FactoryClass;
import com.synectiks.process.server.plugin.inputs.codecs.AbstractCodec;
import com.synectiks.process.server.plugin.inputs.codecs.CodecAggregator;
import com.synectiks.process.server.plugin.journal.RawMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Codec(name = "jsonpath", displayName = "JSON Path")
public class JsonPathCodec extends AbstractCodec {

    public static final String CK_PATH = "path";
    public static final String CK_SOURCE = "source";

    private final JsonPath jsonPath;

    @AssistedInject
    public JsonPathCodec(@Assisted Configuration configuration) {
        super(configuration);
        final String pathString = configuration.getString(CK_PATH);
        jsonPath = pathString == null ? null : JsonPath.compile(pathString);
    }

    @Nullable
    @Override
    public Message decode(@Nonnull RawMessage rawMessage) {
        if (jsonPath == null) {
            return null;
        }
        final String json = new String(rawMessage.getPayload(), StandardCharsets.UTF_8);
        final Map<String, Object> fields = read(json);

        final Message message = new Message(buildShortMessage(fields),
                                            configuration.getString(CK_SOURCE),
                                            rawMessage.getTimestamp());
        message.addFields(fields);
        return message;
    }

    @VisibleForTesting
    protected Map<String, Object> read(String json) {
        final Object result = jsonPath.read(json);

        final Map<String, Object> fields = Maps.newHashMap();

        if (result instanceof Integer || result instanceof Double || result instanceof Long) {
            fields.put("result", result);
        } else if (result instanceof List) {
            final List list = (List) result;
            if (!list.isEmpty()) {
                fields.put("result", list.get(0).toString());
            }
        } else {
            // Now it's most likely a string or something we do not map.
            fields.put("result", result.toString());
        }
        return fields;
    }

    @VisibleForTesting
    protected String buildShortMessage(Map<String, Object> fields) {
        final StringBuilder shortMessage = new StringBuilder();
        shortMessage.append("JSON API poll result: ");
        shortMessage.append(jsonPath.getPath()).append(" -> ");
        if (fields.toString().length() > 50) {
            shortMessage.append(fields.toString().substring(0, 50)).append("[...]");
        } else {
            shortMessage.append(fields.toString());
        }

        return shortMessage.toString();
    }

    @Nullable
    @Override
    public CodecAggregator getAggregator() {
        return null;
    }

    @FactoryClass
    public interface Factory extends AbstractCodec.Factory<JsonPathCodec> {
        @Override
        JsonPathCodec create(Configuration configuration);

        @Override
        Config getConfig();

        @Override
        Descriptor getDescriptor();
    }

    @ConfigClass
    public static class Config extends AbstractCodec.Config {
        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            final ConfigurationRequest r = super.getRequestedConfiguration();

            r.addField(new TextField(
                    CK_PATH,
                    "JSON path of data to extract",
                    "$.store.book[1].number_of_orders",
                    "Path to the value you want to extract from the JSON response. Take a look at the documentation for a more detailed explanation.",
                    ConfigurationField.Optional.NOT_OPTIONAL
            ));

            r.addField(new TextField(
                    CK_SOURCE,
                    "Message source",
                    "yourapi",
                    "What to use as source field of the resulting message.",
                    ConfigurationField.Optional.NOT_OPTIONAL
            ));

            return r;        }

        @Override
        public void overrideDefaultValues(@Nonnull ConfigurationRequest cr) {

        }
    }

    public static class Descriptor extends AbstractCodec.Descriptor {
        @Inject
        public Descriptor() {
            super(JsonPathCodec.class.getAnnotation(Codec.class).displayName());
        }
    }
}
