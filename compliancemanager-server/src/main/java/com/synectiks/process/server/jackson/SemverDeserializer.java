/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.synectiks.process.server.semver4j.Semver;
import com.synectiks.process.server.semver4j.SemverException;

import java.io.IOException;

public class SemverDeserializer extends StdDeserializer<Semver> {
    private final Semver.SemverType semverType;

    public SemverDeserializer() {
        this(Semver.SemverType.NPM);
    }

    public SemverDeserializer(Semver.SemverType semverType) {
        super(Semver.class);
        this.semverType = semverType;
    }

    @Override
    public Semver deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        switch (p.getCurrentTokenId()) {
            case JsonTokenId.ID_STRING:
            case JsonTokenId.ID_NUMBER_INT:
                final String str = p.getText().trim();
                try {
                    return buildSemver(str);
                } catch (SemverException e) {
                    ctxt.reportMappingException(e.getMessage());
                }
            default:
                throw ctxt.wrongTokenException(p, JsonToken.VALUE_STRING, "expected String or Number");
        }
    }

    private Semver buildSemver(String s) {
        return new Semver(s, semverType);
    }
}