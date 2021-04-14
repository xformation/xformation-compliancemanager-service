/*
 * */
package com.synectiks.process.server.inputs.converters;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.synectiks.process.server.plugin.inputs.Converter;

import static com.google.common.base.Strings.isNullOrEmpty;

public class TokenizerConverter extends Converter {
    // ┻━┻ ︵ ¯\(ツ)/¯ ︵ ┻━┻
    private static final Pattern PATTERN = Pattern.compile("(?:^|\\s)(?:([\\w-]+)\\s?=\\s?((?:\"[^\"]+\")|(?:'[^']+')|(?:[\\S]+)))");

    public TokenizerConverter(Map<String, Object> config) {
        super(Type.TOKENIZER, config);
    }

    @Override
    public Object convert(String value) {
        if (isNullOrEmpty(value)) {
            return value;
        }

        if (value.contains("=")) {
            final Map<String, String> fields = new HashMap<>();

            Matcher m = PATTERN.matcher(value);
            while (m.find()) {
                if (m.groupCount() != 2) {
                    continue;
                }

                fields.put(removeQuotes(m.group(1)), removeQuotes(m.group(2)));
            }

            return fields;
        } else {
            return Collections.emptyMap();
        }
    }

    private String removeQuotes(String s) {
        final boolean doubleQuotes = s.startsWith("\"") && s.endsWith("\"");
        final boolean singleQuotes = s.startsWith("'") && s.endsWith("'");
        if (doubleQuotes || singleQuotes) {
            return s.substring(1, s.length() - 1);
        } else {
            return s;
        }
    }

    @Override
    public boolean buildsMultipleFields() {
        return true;
    }
}
