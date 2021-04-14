/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.joschi.jadconfig.util.Duration;
import com.synectiks.process.common.storage.elasticsearch6.ClusterAdapterES6;
import com.synectiks.process.common.storage.elasticsearch6.jest.JestUtils;
import com.synectiks.process.common.storage.elasticsearch6.testing.ElasticsearchInstanceES6;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;
import com.synectiks.process.server.indexer.cluster.ClusterAdapter;
import com.synectiks.process.server.indexer.cluster.ClusterIT;

import io.searchbox.core.Cat;
import io.searchbox.core.CatResult;

import org.junit.Rule;

import static com.synectiks.process.common.storage.elasticsearch6.testing.TestUtils.jestClient;
import static org.assertj.core.api.Assertions.assertThat;

public class ClusterES6IT extends ClusterIT {
    @Rule
    public final ElasticsearchInstance elasticsearch = ElasticsearchInstanceES6.create();

    @Override
    protected ClusterAdapter clusterAdapter(Duration timeout) {
        return new ClusterAdapterES6(jestClient(elasticsearch), timeout);
    }

    @Override
    protected ElasticsearchInstance elasticsearch() {
        return this.elasticsearch;
    }

    @Override
    protected String currentNodeId() {
        final JsonNode node = currentNodeInfo();
        return node.get("id").asText();
    }

    private JsonNode currentNodeInfo() {
        final Cat nodesInfo = new Cat.NodesBuilder()
                .setParameter("h", "id,name,host,ip")
                .setParameter("format", "json")
                .setParameter("full_id", "true")
                .build();
        final CatResult catResult = JestUtils.execute(jestClient(elasticsearch), nodesInfo, () -> "Unable to retrieve current node info");
        final JsonNode result = catResult.getJsonObject().path("result");
        assertThat(result).isNotEmpty();

        return result.path(0);
    }

    @Override
    protected String currentNodeName() {
        final JsonNode node = currentNodeInfo();
        return node.get("name").asText();
    }

    @Override
    protected String currentHostnameOrIp() {
        final JsonNode node = currentNodeInfo();
        final String ip = node.path("ip").asText();
        return node.path("host").asText(ip);
    }
}
