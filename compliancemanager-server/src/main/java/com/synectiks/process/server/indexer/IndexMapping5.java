/*
 * */
package com.synectiks.process.server.indexer;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Representing the message type mapping in Elasticsearch 5.x. This is giving ES more
 * information about what the fields look like and how it should analyze them.
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/5.4/mapping.html">Elasticsearch Reference / Mapping</a>
 */
class IndexMapping5 extends IndexMapping {
    @Override
    Map<String, Object> dynamicStrings() {
        return ImmutableMap.of(
                // Match all
                "match", "*",
                // Analyze nothing by default
                "mapping", ImmutableMap.of("index", "not_analyzed"));
    }
}
