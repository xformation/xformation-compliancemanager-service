/*
 * */
package com.synectiks.process.server.shared.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.joschi.jadconfig.util.Size;

import java.io.IOException;

/**
 * Serializes JadConfig's Size utility object to bytes.
 */
public class SizeSerializer extends JsonSerializer<Size> {
    @Override
    public Class<Size> handledType() {
        return Size.class;
    }

    @Override
    public void serialize(Size value,
                          JsonGenerator jgen,
                          SerializerProvider provider) throws IOException {
        jgen.writeNumber(value.toBytes());
    }
}
