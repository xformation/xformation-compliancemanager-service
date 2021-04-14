/*
 * */
package com.synectiks.process.server.indexer;

import java.util.Map;

import com.synectiks.process.server.indexer.indexset.IndexSetConfig;

/**
 * Implementing classes provide an index mapping template representation that can be stored in Elasticsearch.
 */
public interface IndexMappingTemplate {
    /**
     * Returns the index template as a map.
     *
     * @param indexSetConfig the index set configuration
     * @param indexPattern   the index pattern the returned template should be applied to
     * @param order          the order value of the index template
     * @return the index template
     */
    Map<String, Object> toTemplate(IndexSetConfig indexSetConfig, String indexPattern, int order);

    /**
     * Returns the index template as a map. (with an default order of -1)
     *
     * @param indexSetConfig the index set configuration
     * @param indexPattern   the index pattern the returned template should be applied to
     * @return the index template
     */
    default Map<String, Object> toTemplate(IndexSetConfig indexSetConfig, String indexPattern) {
        return toTemplate(indexSetConfig, indexPattern, -1);
    }
}
