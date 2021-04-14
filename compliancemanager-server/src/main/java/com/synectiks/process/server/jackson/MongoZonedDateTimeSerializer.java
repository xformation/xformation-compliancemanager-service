/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public final class MongoZonedDateTimeSerializer extends StdScalarSerializer<ZonedDateTime> {
    public MongoZonedDateTimeSerializer() {
        super(ZonedDateTime.class);
    }

    @Override
    public void serialize(ZonedDateTime zonedDateTime,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        final Instant instant = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toInstant();
        final Date date = Date.from(instant);
        jsonGenerator.writeObject(date);
    }
}