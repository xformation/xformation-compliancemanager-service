/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6.testing;

import org.junit.Rule;
import org.junit.Test;

import com.synectiks.process.common.testing.elasticsearch.ElasticsearchBaseTest;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;

import static org.assertj.core.api.Assertions.assertThat;

public class ElasticsearchBaseTestIT extends ElasticsearchBaseTest {
    @Rule
    public final ElasticsearchInstance elasticsearch = ElasticsearchInstanceES6.create();

    @Override
    protected ElasticsearchInstance elasticsearch() {
        return this.elasticsearch;
    }

    @Test
    public void clientsAreConstructed() {
        assertThat(((ElasticsearchInstanceES6)elasticsearch).jestClient()).isNotNull();
        assertThat(client()).isNotNull();
    }
}
