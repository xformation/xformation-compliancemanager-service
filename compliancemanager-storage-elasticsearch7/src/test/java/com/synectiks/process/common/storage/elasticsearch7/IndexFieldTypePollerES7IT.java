/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch7;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synectiks.process.common.storage.elasticsearch7.ElasticsearchClient;
import com.synectiks.process.common.storage.elasticsearch7.IndexFieldTypePollerAdapterES7;
import com.synectiks.process.common.storage.elasticsearch7.IndicesAdapterES7;
import com.synectiks.process.common.storage.elasticsearch7.cat.CatApi;
import com.synectiks.process.common.storage.elasticsearch7.cluster.ClusterStateApi;
import com.synectiks.process.common.storage.elasticsearch7.mapping.FieldMappingApi;
import com.synectiks.process.common.storage.elasticsearch7.stats.StatsApi;
import com.synectiks.process.common.storage.elasticsearch7.testing.ElasticsearchInstanceES7;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;
import com.synectiks.process.server.indexer.fieldtypes.IndexFieldTypePollerAdapter;
import com.synectiks.process.server.indexer.fieldtypes.IndexFieldTypePollerIT;
import com.synectiks.process.server.indexer.indices.IndicesAdapter;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;
import org.junit.Rule;

public class IndexFieldTypePollerES7IT extends IndexFieldTypePollerIT {
    @Rule
    public final ElasticsearchInstanceES7 elasticsearch = ElasticsearchInstanceES7.create();

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Override
    protected IndicesAdapter createIndicesAdapter() {
        final ElasticsearchClient client = elasticsearch.elasticsearchClient();
        return new IndicesAdapterES7(
                client,
                new StatsApi(objectMapper, client),
                new CatApi(objectMapper, client),
                new ClusterStateApi(objectMapper, client)
        );
    }

    @Override
    protected IndexFieldTypePollerAdapter createIndexFieldTypePollerAdapter() {
        final ElasticsearchClient client = elasticsearch.elasticsearchClient();
        return new IndexFieldTypePollerAdapterES7(client, new FieldMappingApi(objectMapper, client));
    }

    @Override
    protected ElasticsearchInstance elasticsearch() {
        return this.elasticsearch;
    }
}
