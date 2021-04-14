/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6;

import static com.synectiks.process.common.storage.elasticsearch6.testing.TestUtils.jestClient;

import org.junit.Rule;

import com.synectiks.process.common.storage.elasticsearch6.IndexingHelper;
import com.synectiks.process.common.storage.elasticsearch6.IndicesAdapterES6;
import com.synectiks.process.common.storage.elasticsearch6.testing.ElasticsearchInstanceES6;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;
import com.synectiks.process.server.indexer.indices.IndicesAdapter;
import com.synectiks.process.server.indexer.indices.IndicesGetAllMessageFieldsIT;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

public class IndicesGetAllMessageFieldsES6IT extends IndicesGetAllMessageFieldsIT {
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
}
