/*
 * */
package com.synectiks.process.server.inputs.converters;

import java.util.Locale;
import java.util.Map;

import com.synectiks.process.server.plugin.inputs.Converter;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class UppercaseConverter extends Converter {

    public UppercaseConverter(Map<String, Object> config) {
        super(Type.UPPERCASE, config);
    }

    @Override
    public Object convert(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        return value.toUpperCase(Locale.ENGLISH);
    }

    @Override
    public boolean buildsMultipleFields() {
        return false;
    }
}
