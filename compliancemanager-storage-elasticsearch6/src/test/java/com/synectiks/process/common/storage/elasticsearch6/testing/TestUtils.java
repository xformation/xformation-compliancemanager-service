/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6.testing;

import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;

import io.searchbox.client.JestClient;

public class TestUtils {
    public static JestClient jestClient(ElasticsearchInstance elasticsearchInstance) {
        if (elasticsearchInstance instanceof ElasticsearchInstanceES6) {
            return ((ElasticsearchInstanceES6) elasticsearchInstance).jestClient();
        }

        throw new RuntimeException("Unable to return Jest client, Elasticsearch instance is of wrong type: " + elasticsearchInstance);
    }
}
