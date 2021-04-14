/*
 * */
package com.synectiks.process.server.contentpacks.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueType;

import java.io.IOException;
import java.util.Locale;

public class ValueTypeSerializer extends StdSerializer<ValueType> {
    public ValueTypeSerializer() {
        super(ValueType.class);
    }

    @Override
    public void serialize(ValueType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.name().toLowerCase(Locale.ROOT));
    }
}
