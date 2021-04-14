/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.util.Date;

public final class MongoJodaDateTimeSerializer extends StdScalarSerializer<DateTime> {
    public MongoJodaDateTimeSerializer() {
        super(DateTime.class);
    }

    @Override
    public void serialize(DateTime dateTime,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        final Date date = dateTime.withZone(DateTimeZone.UTC).toDate();
        jsonGenerator.writeObject(date);
    }
}