/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synectiks.process.common.storage.elasticsearch6.ScrollResultES6;
import com.synectiks.process.common.storage.elasticsearch6.jest.JestUtils;
import com.synectiks.process.common.storage.elasticsearch6.testing.ElasticsearchInstanceES6;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchBaseTest;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;
import com.synectiks.process.server.indexer.IndexMapping;
import com.synectiks.process.server.indexer.results.ScrollResult;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.params.Parameters;
import org.graylog.shaded.elasticsearch5.org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;
import java.util.Collections;

import static com.synectiks.process.common.storage.elasticsearch6.testing.TestUtils.jestClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.graylog.shaded.elasticsearch5.org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

public class ScrollResultES6IT extends ElasticsearchBaseTest {
    @Rule
    public final ElasticsearchInstance elasticsearch = ElasticsearchInstanceES6.create();

    @Override
    protected ElasticsearchInstance elasticsearch() {
        return this.elasticsearch;
    }

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final String INDEX_NAME = "graylog_0";

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Test
    public void nextChunkDoesNotContainJestMetadata() throws IOException {
        importFixture("ScrollResultIT.json");

        final String query = SearchSourceBuilder.searchSource().query(matchAllQuery()).toString();
        final Search request = new Search.Builder(query)
                .addIndex(INDEX_NAME)
                .addType(IndexMapping.TYPE_MESSAGE)
                .setParameter(Parameters.SCROLL, "1m")
                .setParameter(Parameters.SIZE, 5)
                .build();
        final SearchResult searchResult = JestUtils.execute(jestClient(elasticsearch), request, () -> "Exception");

        assertThat(jestClient(elasticsearch)).isNotNull();
        final ScrollResult scrollResult = new ScrollResultES6(jestClient(elasticsearch), objectMapper, searchResult,
                "*", Collections.singletonList("message"), -1);
        scrollResult.nextChunk().getMessages().forEach(
                message -> assertThat(message.getMessage().getFields()).doesNotContainKeys("es_metadata_id", "es_metadata_version")
        );
        scrollResult.nextChunk().getMessages().forEach(
                message -> assertThat(message.getMessage().getFields()).doesNotContainKeys("es_metadata_id", "es_metadata_version")
        );
        assertThat(scrollResult.nextChunk()).isNull();
    }
}
