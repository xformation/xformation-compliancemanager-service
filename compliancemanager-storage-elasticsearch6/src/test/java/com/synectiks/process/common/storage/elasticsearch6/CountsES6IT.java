/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6;

import static com.synectiks.process.common.storage.elasticsearch6.testing.TestUtils.jestClient;

import org.junit.Rule;

import com.synectiks.process.common.storage.elasticsearch6.CountsAdapterES6;
import com.synectiks.process.common.storage.elasticsearch6.testing.ElasticsearchInstanceES6;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;
import com.synectiks.process.server.indexer.counts.CountsAdapter;
import com.synectiks.process.server.indexer.counts.CountsIT;

public class CountsES6IT extends CountsIT {
    @Rule
    public final ElasticsearchInstance elasticsearch = ElasticsearchInstanceES6.create();

    @Override
    protected ElasticsearchInstance elasticsearch() {
        return this.elasticsearch;
    }

    @Override
    protected CountsAdapter countsAdapter() {
        return new CountsAdapterES6(jestClient(elasticsearch));
    }
}
