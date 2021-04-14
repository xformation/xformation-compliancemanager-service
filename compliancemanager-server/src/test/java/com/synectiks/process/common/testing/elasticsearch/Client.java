/*
 * */
package com.synectiks.process.common.testing.elasticsearch;

import java.util.Map;

public interface Client {
    default void createIndex(String index) {
        createIndex(index, 1, 0);
    }

    void createIndex(String index, int shards, int replicas);

    default String createRandomIndex(String prefix) {
        final String indexName = prefix + System.nanoTime();

        createIndex(indexName);
        waitForGreenStatus(indexName);

        return indexName;
    }

    void deleteIndices(String... indices);

    void closeIndex(String index);

    boolean indicesExists(String... indices);

    void addAliasMapping(String indexName, String alias);

    boolean templateExists(String templateName);

    void putTemplate(String templateName, Map<String, Object> source);

    void deleteTemplates(String... templates);

    void waitForGreenStatus(String... indices);

    void refreshNode();

    void bulkIndex(BulkIndexRequest bulkIndexRequest);

    void cleanUp();

    String fieldType(String testIndexName, String source);

    void putSetting(String setting, String value);

    void waitForIndexBlock(String index);

    void resetIndexBlock(String index);
}
