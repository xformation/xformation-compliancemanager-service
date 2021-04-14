/*
 * */
package com.synectiks.process.server.inputs.extractors;

import com.codahale.metrics.MetricRegistry;
import com.synectiks.process.server.plugin.inputs.Converter;
import com.synectiks.process.server.plugin.inputs.Extractor;

import java.util.List;
import java.util.Map;

public class CopyInputExtractor extends Extractor {
    public CopyInputExtractor(MetricRegistry metricRegistry, String id, String title, long order, CursorStrategy cursorStrategy, String sourceField, String targetField, Map<String, Object> extractorConfig, String creatorUserId, List<Converter> converters, ConditionType conditionType, String conditionValue) throws ReservedFieldException {
        super(metricRegistry,
              id,
              title,
              order,
              Type.COPY_INPUT,
              cursorStrategy,
              sourceField,
              targetField,
              extractorConfig,
              creatorUserId,
              converters,
              conditionType,
              conditionValue);
    }

    @Override
    protected Result[] run(String value) {
        return new Result[] { new Result(value, 0, value.length())};
    }
}
