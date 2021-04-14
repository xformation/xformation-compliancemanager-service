/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synectiks.process.common.storage.elasticsearch6.IndexFieldTypePollerAdapterES6;
import com.synectiks.process.common.storage.elasticsearch6.IndexingHelper;
import com.synectiks.process.common.storage.elasticsearch6.IndicesAdapterES6;
import com.synectiks.process.common.storage.elasticsearch6.testing.ElasticsearchInstanceES6;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;
import com.synectiks.process.server.indexer.fieldtypes.IndexFieldTypePollerAdapter;
import com.synectiks.process.server.indexer.fieldtypes.IndexFieldTypePollerIT;
import com.synectiks.process.server.indexer.indices.IndicesAdapter;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import static com.synectiks.process.common.storage.elasticsearch6.testing.TestUtils.jestClient;

import org.junit.Rule;

public class IndexFieldTypePollerES6IT extends IndexFieldTypePollerIT {
    @Rule
    public final ElasticsearchInstance elasticsearch = ElasticsearchInstanceES6.create();

    @Override
    protected ElasticsearchInstance elasticsearch() {
        return this.elasticsearch;
    }

    @Override
    protected IndicesAdapter createIndicesAdapter() {
        final ObjectMapper objectMapper = new ObjectMapperProvider().get();
        return new IndicesAdapterES6(jestClient(elasticsearch), objectMapper, new IndexingHelper());
    }

    @Override
    protected IndexFieldTypePollerAdapter createIndexFieldTypePollerAdapter() {
        return new IndexFieldTypePollerAdapterES6(jestClient(elasticsearch));
    }
}
