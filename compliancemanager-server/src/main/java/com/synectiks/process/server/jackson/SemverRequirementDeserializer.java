/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.synectiks.process.server.semver4j.Requirement;
import com.synectiks.process.server.semver4j.Semver;
import com.synectiks.process.server.semver4j.SemverException;

import java.io.IOException;

public class SemverRequirementDeserializer extends StdDeserializer<Requirement> {
    private final Semver.SemverType semverType;

    public SemverRequirementDeserializer() {
        this(Semver.SemverType.NPM);
    }

    public SemverRequirementDeserializer(Semver.SemverType semverType) {
        super(Requirement.class);
        this.semverType = semverType;
    }

    @Override
    public Requirement deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        switch (p.getCurrentTokenId()) {
            case JsonTokenId.ID_STRING:
                final String str = p.getText().trim();
                try {
                    return buildRequirement(str);
                } catch (SemverException e) {
                    ctxt.reportMappingException(e.getMessage());
                }
            default:
                throw ctxt.wrongTokenException(p, JsonToken.VALUE_STRING, null);
        }
    }

    private Requirement buildRequirement(String s) {
        switch (semverType) {
            case STRICT:
                return Requirement.buildStrict(s);
            case LOOSE:
                return Requirement.buildLoose(s);
            case NPM:
                return Requirement.buildNPM(s);
            case COCOAPODS:
                return Requirement.buildCocoapods(s);
            case IVY:
                return Requirement.buildIvy(s);
            default:
                return null;
        }
    }
}