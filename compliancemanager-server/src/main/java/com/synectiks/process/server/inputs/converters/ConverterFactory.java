/*
 * */
package com.synectiks.process.server.inputs.converters;

import com.synectiks.process.server.ConfigurationException;
import com.synectiks.process.server.lookup.LookupTableService;
import com.synectiks.process.server.plugin.inputs.Converter;

import javax.inject.Inject;
import java.util.Map;

public class ConverterFactory {
    private final LookupTableService lookupTableService;

    @Inject
    public ConverterFactory(final LookupTableService lookupTableService) {
        this.lookupTableService = lookupTableService;
    }

    public Converter create(Converter.Type type, Map<String, Object> config) throws NoSuchConverterException, ConfigurationException {
        switch (type) {
            case NUMERIC:
                return new NumericConverter(config);
            case DATE:
                return new DateConverter(config);
            case HASH:
                return new HashConverter(config);
            case SPLIT_AND_COUNT:
                return new SplitAndCountConverter(config);
            case SYSLOG_PRI_LEVEL:
                return new SyslogPriLevelConverter(config);
            case SYSLOG_PRI_FACILITY:
                return new SyslogPriFacilityConverter(config);
            case IP_ANONYMIZER:
                return new IPAnonymizerConverter(config);
            case TOKENIZER:
                return new TokenizerConverter(config);
            case CSV:
                return new CsvConverter(config);
            case LOWERCASE:
                return new LowercaseConverter(config);
            case UPPERCASE:
                return new UppercaseConverter(config);
            case FLEXDATE:
                return new FlexibleDateConverter(config);
            case LOOKUP_TABLE:
                return new LookupTableConverter(config, lookupTableService);
            default:
                throw new NoSuchConverterException();
        }
    }

    public static class NoSuchConverterException extends Exception {
    }
}
