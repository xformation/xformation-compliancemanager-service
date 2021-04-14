/*
 * */
package com.synectiks.process.server.inputs.extractors;

import com.codahale.metrics.MetricRegistry;
import com.synectiks.process.server.ConfigurationException;
import com.synectiks.process.server.lookup.LookupTableService;
import com.synectiks.process.server.plugin.inputs.Converter;
import com.synectiks.process.server.plugin.inputs.Extractor;
import com.synectiks.process.server.plugin.lookup.LookupResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

public class LookupTableExtractor extends Extractor {
    private final LookupTableService.Function lookupTable;
    public static final String CONFIG_LUT_NAME = "lookup_table_name";

    public LookupTableExtractor(final MetricRegistry metricRegistry,
                                final LookupTableService lookupTableService,
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
        super(metricRegistry, id, title, order, Type.LOOKUP_TABLE, cursorStrategy, sourceField, targetField, extractorConfig, creatorUserId, converters, conditionType, conditionValue);

        final String lookupTableName = (String) extractorConfig.get(CONFIG_LUT_NAME);
        if (isNullOrEmpty(lookupTableName)) {
            throw new ConfigurationException("Missing lookup table extractor configuration field: " + CONFIG_LUT_NAME);
        }

        if (!lookupTableService.hasTable(lookupTableName)) {
            throw new IllegalStateException("Configured lookup table <" + lookupTableName + "> doesn't exist");
        }

        this.lookupTable = lookupTableService.newBuilder().lookupTable(lookupTableName).build();
    }

    @Override
    @Nullable
    protected Result[] run(String sourceFieldValue) {
        final LookupResult result = lookupTable.lookup(sourceFieldValue);

        if (result == null || result.isEmpty()) {
            return null;
        }

        final Object value = result.singleValue();
        if (value == null) {
            return null;
        }

        return new Result[]{ new Result(value, targetField, -1, -1) };
    }
}
