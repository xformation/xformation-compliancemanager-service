/*
 * */
package com.synectiks.process.server.indexer;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class GIMMapping7 extends GIMMapping {
    @Override
    protected Map<String, Object> mapping(String analyzer) {
        return messageMapping(analyzer);
    }

    @Override
    Map<String, Object> dynamicStrings() {
        return ImmutableMap.of(
                "match_mapping_type", "string",
                "mapping", notAnalyzedString()
        );
    }

    @Override
    Map<String, Object> createTemplate(String template, int order, Map<String, Object> settings, Map<String, Object> mappings) {
        return ImmutableMap.of(
                "index_patterns", template,
                "order", order,
                "settings", settings,
                "mappings", mappings
        );
    }
}
