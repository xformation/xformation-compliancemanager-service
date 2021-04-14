/*
 * */
package com.synectiks.process.server.inputs.converters;

import java.util.Map;
import java.util.regex.Pattern;

import com.synectiks.process.server.plugin.inputs.Converter;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class IPAnonymizerConverter extends Converter {

    public static final String REPLACEMENT = "$1.$2.$3.xxx";
    public static final Pattern p = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})");

    public IPAnonymizerConverter(Map<String, Object> config) {
        super(Type.IP_ANONYMIZER, config);
    }

    @Override
    public Object convert(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        return p.matcher(value).replaceAll(REPLACEMENT);
    }

    @Override
    public boolean buildsMultipleFields() {
        return false;
    }

}
