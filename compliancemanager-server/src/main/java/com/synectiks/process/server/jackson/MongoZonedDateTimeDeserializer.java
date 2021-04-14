/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

public final class MongoZonedDateTimeDeserializer extends StdScalarDeserializer<ZonedDateTime> {
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .appendOffset("+HHmm", "Z")
            .toFormatter();

    public MongoZonedDateTimeDeserializer() {
        super(ZonedDateTime.class);
    }

    @Override
    public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        switch (jsonParser.currentToken()) {
            case VALUE_EMBEDDED_OBJECT:
                final Object embeddedObject = jsonParser.getEmbeddedObject();
                if (embeddedObject instanceof Date) {
                    final Date date = (Date) embeddedObject;
                    return ZonedDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
                } else {
                    throw new IllegalStateException("Unsupported token: " + jsonParser.currentToken());
                }
            case VALUE_STRING:
                final String text = jsonParser.getText();
                return ZonedDateTime.parse(text, FORMATTER).withZoneSameInstant(ZoneOffset.UTC);
            default:
                throw new IllegalStateException("Unsupported token: " + jsonParser.currentToken());
        }
    }
}