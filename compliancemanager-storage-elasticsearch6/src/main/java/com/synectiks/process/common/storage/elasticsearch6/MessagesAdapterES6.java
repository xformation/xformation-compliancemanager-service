/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6;

import static com.codahale.metrics.MetricRegistry.name;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Iterables;
import com.synectiks.process.common.storage.elasticsearch6.jest.JestUtils;
import com.synectiks.process.server.indexer.IndexMapping;
import com.synectiks.process.server.indexer.messages.ChunkedBulkIndexer;
import com.synectiks.process.server.indexer.messages.DocumentNotFoundException;
import com.synectiks.process.server.indexer.messages.Indexable;
import com.synectiks.process.server.indexer.messages.IndexingRequest;
import com.synectiks.process.server.indexer.messages.Messages;
import com.synectiks.process.server.indexer.messages.MessagesAdapter;
import com.synectiks.process.server.indexer.results.ResultMessage;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.Tools;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.indices.Analyze;

public class MessagesAdapterES6 implements MessagesAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(MessagesAdapterES6.class);

    static final String INDEX_BLOCK_ERROR = "cluster_block_exception";
    static final String INDEX_BLOCK_REASON = "blocked by: [FORBIDDEN/12/index read-only / allow delete (api)];";
    static final String MAPPER_PARSING_EXCEPTION = "mapper_parsing_exception";
    static final String UNAVAILABLE_SHARDS_EXCEPTION = "unavailable_shards_exception";
    static final String PRIMARY_SHARD_NOT_ACTIVE_REASON = "primary shard is not active";

    private final JestClient client;
    private final boolean useExpectContinue;

    private final Meter invalidTimestampMeter;
    private final ChunkedBulkIndexer chunkedBulkIndexer;
    private final ObjectMapper objectMapper;

    @Inject
    public MessagesAdapterES6(JestClient client,
                              @Named("elasticsearch_use_expect_continue") boolean useExpectContinue,
                              MetricRegistry metricRegistry,
                              ChunkedBulkIndexer chunkedBulkIndexer,
                              ObjectMapper objectMapper) {
        this.client = client;
        this.useExpectContinue = useExpectContinue;
        invalidTimestampMeter = metricRegistry.meter(name(Messages.class, "invalid-timestamps"));
        this.chunkedBulkIndexer = chunkedBulkIndexer;
        this.objectMapper = objectMapper;
    }

    @Override
    public ResultMessage get(String messageId, String index) throws IOException, DocumentNotFoundException {
        final Get get = new Get.Builder(index, messageId).type(IndexMapping.TYPE_MESSAGE).build();
        final DocumentResult result = client.execute(get);

        if (!result.isSucceeded()) {
            throw new DocumentNotFoundException(index, messageId);
        }
//        new io.searchbox.core.Index.Builder(result).index(index).id(messageId).build()
        @SuppressWarnings("unchecked") final Map<String, Object> message = (Map<String, Object>) result.getSourceAsObject(Map.class, false);

        return ResultMessage.parseFromSource(result.getId(), result.getIndex(), message);
    }

    @Override
    public ResultMessage updateDocument(String status, String indexName, String documentId) throws IOException, DocumentNotFoundException {
    	LOG.info("Updating a document: ");
    	
    	final Get get = new Get.Builder(indexName, documentId).type(IndexMapping.TYPE_MESSAGE).build();
        final DocumentResult dr = client.execute(get);
        
    	LOG.info("Index: "+indexName+", Document id: "+ documentId+", status: "+status);
    	
    	JsonNode jn = dr.getJsonObject();
    	String src = jn.findValue("_source").get("message").asText();
    	src = src.replaceAll("New",status);

    	ObjectNode on = ((ObjectNode)jn.findValue("_source")).put("message", src);
    	
    	Index index = new Index.Builder(on).index(indexName).type(IndexMapping.TYPE_MESSAGE).id(documentId).build();
    	final DocumentResult result = client.execute(index);
    	
        if (!result.isSucceeded()) {
        	LOG.error("Document update failed.");
            throw new DocumentNotFoundException(indexName, documentId);
        }
        LOG.info("Document updated successfully: "+result.isSucceeded() );
        return get(documentId, indexName);
    }
    
    @Override
    public List<String> analyze(String toAnalyze, String index, String analyzer) throws IOException {
        final Analyze analyze = new Analyze.Builder().index(index).analyzer(analyzer).text(toAnalyze).build();
        final JestResult result = client.execute(analyze);

        @SuppressWarnings("unchecked") final List<Map<String, Object>> tokens = (List<Map<String, Object>>) result.getValue("tokens");
        final List<String> terms = new ArrayList<>(tokens.size());
        tokens.forEach(token -> terms.add((String) token.get("token")));

        return terms;
    }

    private List<Messages.IndexingError> indexingErrorsFrom(List<BulkResult.BulkResultItem> failedItems, List<IndexingRequest> messageList) {
        if (failedItems.isEmpty()) {
            return Collections.emptyList();
        }

        final Map<String, Indexable> messageMap = messageList.stream()
                .map(IndexingRequest::message)
                .distinct()
                .collect(Collectors.toMap(Indexable::getId, Function.identity()));
        final List<Messages.IndexingError> indexFailures = new ArrayList<>(failedItems.size());
        for (BulkResult.BulkResultItem item : failedItems) {
            LOG.warn("Failed to index message: index=<{}> id=<{}> error=<{}>", item.index, item.id, item.error);

            final Indexable messageEntry = messageMap.get(item.id);

            final Messages.IndexingError indexFailure = indexingErrorFromResultItem(item, messageEntry);

            indexFailures.add(indexFailure);
        }

        return indexFailures;
    }

    private Messages.IndexingError indexingErrorFromResultItem(BulkResult.BulkResultItem item, Indexable message) {
        return Messages.IndexingError.create(message, item.index, errorTypeFromResponse(item), item.errorReason);
    }

    private Messages.IndexingError.ErrorType errorTypeFromResponse(BulkResult.BulkResultItem item) {
        switch (item.errorType) {
            case INDEX_BLOCK_ERROR: return Messages.IndexingError.ErrorType.IndexBlocked;
            case MAPPER_PARSING_EXCEPTION: return Messages.IndexingError.ErrorType.MappingError;
            case UNAVAILABLE_SHARDS_EXCEPTION: if (item.errorReason.contains(PRIMARY_SHARD_NOT_ACTIVE_REASON)) return Messages.IndexingError.ErrorType.IndexBlocked;
            default: return Messages.IndexingError.ErrorType.Unknown;
        }
    }

    private BulkResult runBulkRequest(final Bulk request, int count) throws IOException {
        // Enable Expect-Continue to catch 413 errors before we send the actual data
        final RequestConfig requestConfig = RequestConfig.custom().setExpectContinueEnabled(useExpectContinue).build();
        return JestUtils.execute(client, requestConfig, request);
    }

    @Override
    public List<Messages.IndexingError> bulkIndex(List<IndexingRequest> messageList) throws IOException {
        return chunkedBulkIndexer.index(messageList, this::bulkIndexChunked);
    }

    private List<Messages.IndexingError> bulkIndexChunked(ChunkedBulkIndexer.Chunk command) throws ChunkedBulkIndexer.EntityTooLargeException, IOException {
        final List<IndexingRequest> messageList = command.requests;
        final int offset = command.offset;

        int chunkSize = Math.min(messageList.size(), command.size);

        final List<BulkResult.BulkResultItem> failedItems = new ArrayList<>();
        final Iterable<List<IndexingRequest>> chunks = Iterables.partition(messageList.subList(offset, messageList.size()), chunkSize);
        int chunkCount = 1;
        int indexedSuccessfully = 0;
        for (List<IndexingRequest> chunk : chunks) {
            final BulkResult result = bulkIndexChunk(chunk);

            if (result.getResponseCode() == 413) {
                throw new ChunkedBulkIndexer.EntityTooLargeException(indexedSuccessfully, indexingErrorsFrom(failedItems, messageList));
            }

            // TODO should we check result.isSucceeded()?

            indexedSuccessfully += chunk.size();

            final List<BulkResult.BulkResultItem> remainingFailures = result.getFailedItems();

            failedItems.addAll(remainingFailures);
            if (LOG.isDebugEnabled()) {
                String chunkInfo = "";
                if (chunkSize != messageList.size()) {
                    chunkInfo = String.format(Locale.ROOT, " (chunk %d/%d offset %d)", chunkCount,
                            (int) Math.ceil((double) messageList.size() / chunkSize), offset);
                }
                LOG.debug("Index: Bulk indexed {} messages{}, failures: {}",
                        result.getItems().size(), chunkInfo, failedItems.size());
            }
            if (!remainingFailures.isEmpty()) {
                LOG.error("Failed to index [{}] messages. Please check the index error log in your web interface for the reason. Error: {}",
                        remainingFailures.size(), result.getErrorMessage());
            }
            chunkCount++;
        }
        return indexingErrorsFrom(failedItems, messageList);
    }

    private BulkResult bulkIndexChunk(List<IndexingRequest> chunk) throws IOException {
        final Bulk.Builder bulk = new Bulk.Builder();

        for (IndexingRequest entry : chunk) {
            final Indexable message = entry.message();

            bulk.addAction(new Index.Builder(message.toElasticSearchObject(objectMapper, invalidTimestampMeter))
                    .index(entry.indexSet().getWriteIndexAlias())
                    .type(IndexMapping.TYPE_MESSAGE)
                    .id(message.getId())
                    .build());
        }

        return runBulkRequest(bulk.build(), chunk.size());
    }
}
