/*
 * */
package com.synectiks.process.server.inputs.converters;

import java.util.Locale;
import java.util.Map;

import com.synectiks.process.server.plugin.inputs.Converter;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class LowercaseConverter extends Converter {

    public LowercaseConverter(Map<String, Object> config) {
        super(Type.LOWERCASE, config);
    }

    @Override
    public Object convert(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        return value.toLowerCase(Locale.ENGLISH);
    }

    @Override
    public boolean buildsMultipleFields() {
        return false;
    }
}
