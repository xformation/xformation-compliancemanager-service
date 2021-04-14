/*
 * */
package com.synectiks.process.server.inputs.converters;

import com.synectiks.process.server.ConfigurationException;
import com.synectiks.process.server.plugin.inputs.Converter;

import java.util.Map;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;

public class SplitAndCountConverter extends Converter {
    private final String splitByEscaped;

    public SplitAndCountConverter(Map<String, Object> config) throws ConfigurationException {
        super(Type.SPLIT_AND_COUNT, config);

        final String splitBy = (String) config.get("split_by");
        if (isNullOrEmpty(splitBy)) {
            throw new ConfigurationException("Missing config [split_by].");
        }

        splitByEscaped = Pattern.quote(splitBy);
    }

    @Override
    public Object convert(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }

        return value.split(splitByEscaped).length;
    }

    @Override
    public boolean buildsMultipleFields() {
        return false;
    }

}
