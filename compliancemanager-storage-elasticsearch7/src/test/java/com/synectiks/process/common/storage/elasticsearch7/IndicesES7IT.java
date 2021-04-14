/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch7;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.synectiks.process.common.storage.elasticsearch7.ElasticsearchClient;
import com.synectiks.process.common.storage.elasticsearch7.IndicesAdapterES7;
import com.synectiks.process.common.storage.elasticsearch7.NodeAdapterES7;
import com.synectiks.process.common.storage.elasticsearch7.cat.CatApi;
import com.synectiks.process.common.storage.elasticsearch7.cluster.ClusterStateApi;
import com.synectiks.process.common.storage.elasticsearch7.stats.StatsApi;
import com.synectiks.process.common.storage.elasticsearch7.testing.ElasticsearchInstanceES7;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;
import com.synectiks.process.server.indexer.cluster.NodeAdapter;
import com.synectiks.process.server.indexer.indices.IndicesAdapter;
import com.synectiks.process.server.indexer.indices.IndicesIT;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class IndicesES7IT extends IndicesIT {
    @Rule
    public final ElasticsearchInstanceES7 elasticsearch = ElasticsearchInstanceES7.create();

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Override
    protected ElasticsearchInstance elasticsearch() {
        return elasticsearch;
    }

    @Override
    protected IndicesAdapter indicesAdapter() {
        final ObjectMapper objectMapper = new ObjectMapperProvider().get();
        final ElasticsearchClient client = elasticsearch.elasticsearchClient();
        return new IndicesAdapterES7(
                client,
                new StatsApi(objectMapper, client),
                new CatApi(objectMapper, client),
                new ClusterStateApi(objectMapper, client)
        );
    }

    @Override
    protected NodeAdapter createNodeAdapter() {
        return new NodeAdapterES7(elasticsearch.elasticsearchClient());
    }

    @Override
    protected Map<String, Object> createTemplateFor(String indexWildcard, Map<String, Object> mapping) {
        return ImmutableMap.of(
                "template", indexWildcard,
                "mappings", mapping
        );
    }

    // Prevent accidental use of AliasActions.Type.REMOVE_INDEX,
    // as despite being an *Alias* Action, it actually deletes an index!
    @Test
    public void cyclingAliasLeavesOldIndexInPlace() {
        final String deflector = "indices_it_deflector";

        final String index1 = client().createRandomIndex("indices_it_");
        final String index2 = client().createRandomIndex("indices_it_");

        client().addAliasMapping(index1, deflector);

        indices.cycleAlias(deflector, index2, index1);

        assertThat(indices.exists(index1)).isTrue();
    }
}
