/*
 * */
package com.synectiks.process.server.inputs.extractors;

import com.codahale.metrics.MetricRegistry;
import com.synectiks.process.server.ConfigurationException;
import com.synectiks.process.server.grok.GrokPatternRegistry;
import com.synectiks.process.server.lookup.LookupTableService;
import com.synectiks.process.server.plugin.inputs.Converter;
import com.synectiks.process.server.plugin.inputs.Extractor;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class ExtractorFactory {
    private final MetricRegistry metricRegistry;
    private final GrokPatternRegistry grokPatternRegistry;
    private final LookupTableService lookupTableService;

    @Inject
    public ExtractorFactory(MetricRegistry metricRegistry, GrokPatternRegistry grokPatternRegistry, LookupTableService lookupTableService) {
        this.metricRegistry = metricRegistry;
        this.grokPatternRegistry = grokPatternRegistry;
        this.lookupTableService = lookupTableService;
    }

    public Extractor factory(String id,
                             String title,
                             long order,
                             Extractor.CursorStrategy cursorStrategy,
                             Extractor.Type type,
                             String sourceField,
                             String targetField,
                             Map<String, Object> extractorConfig,
                             String creatorUserId, List<Converter> converters,
                             Extractor.ConditionType conditionType,
                             String conditionValue)
            throws NoSuchExtractorException, Extractor.ReservedFieldException, ConfigurationException {

        // TODO convert to guice factory
        switch (type) {
            case REGEX:
                return new RegexExtractor(metricRegistry, id, title, order, cursorStrategy, sourceField, targetField, extractorConfig, creatorUserId, converters, conditionType, conditionValue);
            case SUBSTRING:
                return new SubstringExtractor(metricRegistry, id, title, order, cursorStrategy, sourceField, targetField, extractorConfig, creatorUserId, converters, conditionType, conditionValue);
            case SPLIT_AND_INDEX:
                return new SplitAndIndexExtractor(metricRegistry, id, title, order, cursorStrategy, sourceField, targetField, extractorConfig, creatorUserId, converters, conditionType, conditionValue);
            case COPY_INPUT:
                return new CopyInputExtractor(metricRegistry, id, title, order, cursorStrategy, sourceField, targetField, extractorConfig, creatorUserId, converters, conditionType, conditionValue);
            case REGEX_REPLACE:
                return new RegexReplaceExtractor(metricRegistry, id, title, order, cursorStrategy, sourceField, targetField, extractorConfig, creatorUserId, converters, conditionType, conditionValue);
            case GROK:
                return new GrokExtractor(metricRegistry, grokPatternRegistry, id, title, order, cursorStrategy, sourceField, targetField, extractorConfig, creatorUserId, converters, conditionType, conditionValue);
            case JSON:
                return new JsonExtractor(metricRegistry, id, title, order, cursorStrategy, sourceField, targetField, extractorConfig, creatorUserId, converters, conditionType, conditionValue);
            case LOOKUP_TABLE:
                return new LookupTableExtractor(metricRegistry, lookupTableService, id, title, order, cursorStrategy, sourceField, targetField, extractorConfig, creatorUserId, converters, conditionType, conditionValue);
            default:
                throw new NoSuchExtractorException();
        }
    }

    public static class NoSuchExtractorException extends Exception {
    }
}
