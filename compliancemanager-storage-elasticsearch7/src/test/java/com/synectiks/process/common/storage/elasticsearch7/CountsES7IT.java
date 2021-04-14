/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch7;

import com.synectiks.process.common.storage.elasticsearch7.CountsAdapterES7;
import com.synectiks.process.common.storage.elasticsearch7.testing.ElasticsearchInstanceES7;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;
import com.synectiks.process.server.indexer.counts.CountsAdapter;
import com.synectiks.process.server.indexer.counts.CountsIT;
import org.junit.Rule;

public class CountsES7IT extends CountsIT {
    @Rule
    public final ElasticsearchInstanceES7 elasticsearch = ElasticsearchInstanceES7.create();

    @Override
    protected ElasticsearchInstance elasticsearch() {
        return this.elasticsearch;
    }

    @Override
    protected CountsAdapter countsAdapter() {
        return new CountsAdapterES7(elasticsearch.elasticsearchClient());
    }
}
