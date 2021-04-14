/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.zafarkhaja.semver.Version;

import java.io.IOException;

public class VersionDeserializer extends StdDeserializer<Version> {
    public VersionDeserializer() {
        super(Version.class);
    }

    @Override
    public Version deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        switch (p.getCurrentTokenId()) {
            case JsonTokenId.ID_STRING:
                final String str = p.getText().trim();
                return Version.valueOf(str);
            case JsonTokenId.ID_NUMBER_INT:
                return Version.forIntegers(p.getIntValue());
        }
        throw ctxt.wrongTokenException(p, JsonToken.VALUE_STRING, "expected String or Number");
    }
}