/*
 * */
package com.synectiks.process.server.indexer;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class GIMMapping6 extends GIMMapping {
    @Override
    Map<String, Object> dynamicStrings() {
        return ImmutableMap.of(
                "match_mapping_type", "string",
                "mapping", notAnalyzedString()
        );
    }
}
