/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6;

import static com.synectiks.process.common.storage.elasticsearch6.testing.TestUtils.jestClient;

import org.junit.Rule;

import com.synectiks.process.common.storage.elasticsearch6.NodeAdapterES6;
import com.synectiks.process.common.storage.elasticsearch6.testing.ElasticsearchInstanceES6;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;
import com.synectiks.process.server.indexer.cluster.NodeAdapter;
import com.synectiks.process.server.indexer.cluster.NodeIT;

public class NodeES6IT extends NodeIT {
    @Rule
    public final ElasticsearchInstance elasticsearch = ElasticsearchInstanceES6.create();

    @Override
    protected ElasticsearchInstance elasticsearch() {
        return this.elasticsearch;
    }

    @Override
    protected NodeAdapter nodeAdapter() {
        return new NodeAdapterES6(jestClient(elasticsearch));
    }
}
