/*
 * */
package com.synectiks.process.server.security.encryption;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class EncryptedValueSerializer extends StdSerializer<EncryptedValue> {
    public EncryptedValueSerializer() {
        super(EncryptedValue.class);
    }

    @Override
    public void serialize(EncryptedValue value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        if (EncryptedValueMapperConfig.isDatabase(provider)) {
            // If we want to store this field into the database, we serialize the actual content
            gen.writeStringField("encrypted_value", value.value());
            gen.writeStringField("salt", value.salt());
        } else {
            // In all other contexts, we just serialize the "is_set" field (e.g. HTTP response)
            gen.writeBooleanField("is_set", value.isSet());
        }

        gen.writeEndObject();
    }
}
