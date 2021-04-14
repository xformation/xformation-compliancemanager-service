/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.zafarkhaja.semver.Version;

import java.io.IOException;

public class VersionSerializer extends StdSerializer<Version> {
    public VersionSerializer() {
        super(Version.class);
    }

    @Override
    public void serialize(final Version value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        final String version = value.toString();
        gen.writeString(version);
    }
}
