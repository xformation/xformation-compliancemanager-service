/*
 * */
package com.synectiks.process.server.inputs.extractors;

import com.codahale.metrics.MetricRegistry;
import com.synectiks.process.server.ConfigurationException;
import com.synectiks.process.server.plugin.inputs.Converter;
import com.synectiks.process.server.plugin.inputs.Extractor;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexExtractor extends Extractor {
    private static final String CONFIG_REGEX_VALUE = "regex_value".intern();

    private final Pattern pattern;

    public RegexExtractor(final MetricRegistry metricRegistry,
                          final String id,
                          final String title,
                          final long order,
                          final CursorStrategy cursorStrategy,
                          final String sourceField,
                          final String targetField,
                          final Map<String, Object> extractorConfig,
                          final String creatorUserId,
                          final List<Converter> converters,
                          final ConditionType conditionType,
                          final String conditionValue) throws ReservedFieldException, ConfigurationException {
        super(metricRegistry, id, title, order, Type.REGEX, cursorStrategy, sourceField, targetField, extractorConfig, creatorUserId, converters, conditionType, conditionValue);

        if (extractorConfig == null || extractorConfig.get(CONFIG_REGEX_VALUE) == null || ((String) extractorConfig.get(CONFIG_REGEX_VALUE)).isEmpty()) {
            throw new ConfigurationException("Missing regex configuration field: regex_value");
        }

        pattern = Pattern.compile((String) extractorConfig.get(CONFIG_REGEX_VALUE), Pattern.DOTALL);
    }

    @Override
    protected Result[] run(String value) {
        final Matcher matcher = pattern.matcher(value);

        if (!matcher.find() || matcher.groupCount() == 0 || matcher.start(1) == -1 || matcher.end(1) == -1) {
            return null;
        }

        return new Result[] { new Result(value.substring(matcher.start(1), matcher.end(1)), matcher.start(1), matcher.end(1)) };
    }

}
