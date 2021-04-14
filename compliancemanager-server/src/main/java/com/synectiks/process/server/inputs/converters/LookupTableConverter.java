/*
 * */
package com.synectiks.process.server.inputs.converters;

import com.synectiks.process.server.ConfigurationException;
import com.synectiks.process.server.lookup.LookupTableService;
import com.synectiks.process.server.plugin.inputs.Converter;
import com.synectiks.process.server.plugin.lookup.LookupResult;

import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

public class LookupTableConverter extends Converter {
    private static final String CONFIG_LOOKUP_TABLE_NAME = "lookup_table_name";

    private final LookupTableService.Function lookupTable;

    public LookupTableConverter(Map<String, Object> config, LookupTableService lookupTableService) throws ConfigurationException {
        super(Type.LOOKUP_TABLE, config);

        final String lookupTableName = (String) config.get(CONFIG_LOOKUP_TABLE_NAME);

        if (isNullOrEmpty(lookupTableName)) {
            throw new ConfigurationException("Missing converter config value: " + CONFIG_LOOKUP_TABLE_NAME);
        }
        if (!lookupTableService.hasTable(lookupTableName)) {
            throw new IllegalStateException("Configured lookup table <" + lookupTableName + "> doesn't exist");
        }

        this.lookupTable = lookupTableService.newBuilder().lookupTable(lookupTableName).build();
    }

    @Override
    public Object convert(String value) {
        final LookupResult result = lookupTable.lookup(value);

        if (result == null || result.isEmpty()) {
            return value;
        }
        return result.singleValue();
    }

    @Override
    public boolean buildsMultipleFields() {
        return false;
    }
}
