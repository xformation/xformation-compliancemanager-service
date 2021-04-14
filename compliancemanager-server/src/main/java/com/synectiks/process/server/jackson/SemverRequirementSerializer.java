/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.synectiks.process.server.semver4j.Requirement;

import java.io.IOException;

public class SemverRequirementSerializer extends StdSerializer<Requirement> {
    public SemverRequirementSerializer() {
        super(Requirement.class);
    }

    @Override
    public void serialize(final Requirement value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        final String s = value.toString();
        gen.writeString(s);
    }
}
