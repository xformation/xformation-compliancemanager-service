/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synectiks.process.common.storage.elasticsearch6.IndexingHelper;
import com.synectiks.process.common.storage.elasticsearch6.MessagesAdapterES6;
import com.synectiks.process.common.storage.elasticsearch6.jest.JestUtils;
import com.synectiks.process.common.storage.elasticsearch6.testing.ElasticsearchInstanceES6;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;
import com.synectiks.process.server.indexer.messages.ChunkedBulkIndexer;
import com.synectiks.process.server.indexer.messages.MessagesAdapter;
import com.synectiks.process.server.indexer.messages.MessagesIT;
import com.synectiks.process.server.indexer.results.ResultMessage;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import io.searchbox.client.JestResult;
import io.searchbox.core.Count;
import io.searchbox.core.CountResult;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;

import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.synectiks.process.common.storage.elasticsearch6.testing.TestUtils.jestClient;
import static org.assertj.core.api.Assertions.assertThat;

public class MessagesES6IT extends MessagesIT {
    @Rule
    public final ElasticsearchInstance elasticsearch = ElasticsearchInstanceES6.create();

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Override
    protected ElasticsearchInstance elasticsearch() {
        return this.elasticsearch;
    }

    private final IndexingHelper indexingHelper = new IndexingHelper();

    @Override
    protected MessagesAdapter createMessagesAdapter(MetricRegistry metricRegistry) {
        return new MessagesAdapterES6(jestClient(elasticsearch), true, metricRegistry, new ChunkedBulkIndexer(), objectMapper);
    }

    @Override
    protected long messageCount(String indexName) {
        final Count count = new Count.Builder().addIndex(indexName).build();

        final CountResult result = JestUtils.execute(jestClient(elasticsearch), count, () -> "Unable to count documents");
        return result.getCount().longValue();
    }

    @Test
    public void getResultDoesNotContainJestMetadataFields() throws Exception {
        final String index = client().createRandomIndex("random");
        final Map<String, Object> source = new HashMap<>();
        source.put("message", "message");
        source.put("source", "source");
        source.put("timestamp", "2017-04-13 15:29:00.000");

        assertThat(indexMessage(index, source, "1")).isTrue();

        final ResultMessage resultMessage = messages.get("1", index);
        final Message message = resultMessage.getMessage();
        assertThat(message).isNotNull();
        assertThat(message.hasField(JestResult.ES_METADATA_ID)).isFalse();
        assertThat(message.hasField(JestResult.ES_METADATA_VERSION)).isFalse();
    }

    @Override
    protected boolean indexMessage(String index, Map<String, Object> source, @SuppressWarnings("SameParameterValue") String id) {
        final Index indexRequest = indexingHelper.prepareIndexRequest(index, source, id);
        final DocumentResult indexResponse = JestUtils.execute(jestClient(elasticsearch), indexRequest, () -> "Unable to index message");

        return indexResponse.isSucceeded();
    }
}
