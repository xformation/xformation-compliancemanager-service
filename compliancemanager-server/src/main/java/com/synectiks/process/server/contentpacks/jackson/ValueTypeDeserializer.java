/*
 * */
package com.synectiks.process.server.contentpacks.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueType;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

public class ValueTypeDeserializer extends StdDeserializer<ValueType> {
    public ValueTypeDeserializer() {
        super(ValueType.class);
    }

    @Override
    public ValueType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentTokenId() == JsonTokenId.ID_STRING) {
            final String str = StringUtils.upperCase(p.getText(), Locale.ROOT);
            try {
                return ValueType.valueOf(str);
            } catch (IllegalArgumentException e) {
                throw ctxt.weirdStringException(str, ValueType.class, e.getMessage());
            }
        } else {
            throw ctxt.wrongTokenException(p, JsonToken.VALUE_STRING, "expected String " + Arrays.toString(ValueType.values()));
        }
    }
}
