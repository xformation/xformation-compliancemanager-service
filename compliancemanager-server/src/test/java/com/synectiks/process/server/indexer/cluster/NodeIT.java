/*
 * */
package com.synectiks.process.server.indexer.cluster;

import org.junit.Before;
import org.junit.Test;

import com.synectiks.process.common.testing.elasticsearch.ElasticsearchBaseTest;
import com.synectiks.process.server.indexer.cluster.Node;
import com.synectiks.process.server.indexer.cluster.NodeAdapter;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class NodeIT extends ElasticsearchBaseTest {
    protected Node node;

    protected abstract NodeAdapter nodeAdapter();

    @Before
    public void setUp() throws Exception {
        this.node = new Node(nodeAdapter());
    }

    @Test
    public void versionReturnsVersionOfCurrentElasticsearch() {
        assertThat(node.getVersion())
                .isNotEmpty()
                .hasValue(elasticsearchVersion());
    }
}
