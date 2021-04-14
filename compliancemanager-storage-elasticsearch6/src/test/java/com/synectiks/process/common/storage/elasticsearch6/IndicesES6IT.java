/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.common.storage.elasticsearch6.IndexingHelper;
import com.synectiks.process.common.storage.elasticsearch6.IndicesAdapterES6;
import com.synectiks.process.common.storage.elasticsearch6.NodeAdapterES6;
import com.synectiks.process.common.storage.elasticsearch6.testing.ElasticsearchInstanceES6;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;
import com.synectiks.process.server.indexer.IndexMapping;
import com.synectiks.process.server.indexer.cluster.NodeAdapter;
import com.synectiks.process.server.indexer.indices.IndicesAdapter;
import com.synectiks.process.server.indexer.indices.IndicesIT;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.junit.Rule;

import static com.synectiks.process.common.storage.elasticsearch6.testing.TestUtils.jestClient;

import java.util.Map;

public class IndicesES6IT extends IndicesIT {
    @Rule
    public final ElasticsearchInstance elasticsearch = ElasticsearchInstanceES6.create();

    @Override
    protected ElasticsearchInstance elasticsearch() {
        return this.elasticsearch;
    }

    @Override
    protected IndicesAdapter indicesAdapter() {
        return new IndicesAdapterES6(jestClient(elasticsearch),
                new ObjectMapperProvider().get(),
                new IndexingHelper());
    }

    @Override
    protected NodeAdapter createNodeAdapter() {
        return new NodeAdapterES6(jestClient(elasticsearch));
    }

    @Override
    protected Map<String, Object> createTemplateFor(String indexWildcard, Map<String, Object> mapping) {
        return ImmutableMap.of(
                "template", indexWildcard,
                "mappings", ImmutableMap.of(IndexMapping.TYPE_MESSAGE, mapping)
        );
    }
}
