/*
 * */
package com.synectiks.process.server.indexer.messages;

import java.io.IOException;
import java.util.List;

import com.synectiks.process.server.indexer.results.ResultMessage;

public interface MessagesAdapter {
    ResultMessage get(String messageId, String index) throws IOException, DocumentNotFoundException;

    List<String> analyze(String toAnalyze, String index, String analyzer) throws IOException;

    List<Messages.IndexingError> bulkIndex(final List<IndexingRequest> messageList) throws IOException;
    
    ResultMessage updateDocument(String status, String indexName, String documentId) throws IOException, DocumentNotFoundException;
}
