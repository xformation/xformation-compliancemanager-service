/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch7;

import com.synectiks.process.common.storage.elasticsearch7.NodeAdapterES7;
import com.synectiks.process.common.storage.elasticsearch7.testing.ElasticsearchInstanceES7;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;
import com.synectiks.process.server.indexer.cluster.NodeAdapter;
import com.synectiks.process.server.indexer.cluster.NodeIT;
import org.junit.Rule;

public class NodeES7IT extends NodeIT {
    @Rule
    public final ElasticsearchInstanceES7 elasticsearch = ElasticsearchInstanceES7.create();

    @Override
    protected ElasticsearchInstance elasticsearch() {
        return this.elasticsearch;
    }

    @Override
    protected NodeAdapter nodeAdapter() {
        return new NodeAdapterES7(elasticsearch.elasticsearchClient());
    }
}
