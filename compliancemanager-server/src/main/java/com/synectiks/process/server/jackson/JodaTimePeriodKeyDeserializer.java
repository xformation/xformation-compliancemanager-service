/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import org.joda.time.format.ISOPeriodFormat;

import java.io.IOException;

public class JodaTimePeriodKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        if (key.length() == 0) {
            return null;
        }
        return ISOPeriodFormat.standard().parsePeriod(key);
    }
}
