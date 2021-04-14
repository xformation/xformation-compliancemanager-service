/*
 * */
package com.synectiks.process.server.indexer;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

class IndexMapping6 extends IndexMapping {
    @Override
    Map<String, Object> dynamicStrings() {
        return ImmutableMap.of(
                "match_mapping_type", "string",
                "mapping", notAnalyzedString()
        );
    }
}
