/*
 * */
package com.synectiks.process.server.shared.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.collect.Range;

import java.io.IOException;

public class RangeJsonSerializer extends JsonSerializer<Range> {
    @Override
    public Class<Range> handledType() {
        return Range.class;
    }

    @Override
    public void serialize(Range range, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        final Integer lower = (Integer) range.lowerEndpoint();
        final Integer upper = (Integer) range.upperEndpoint();
        jgen.writeNumberField("start", lower);
        jgen.writeNumberField("length", upper - lower);
        jgen.writeEndObject();
    }
}
