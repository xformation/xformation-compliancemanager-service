/*
 * */
package com.synectiks.process.server.indexer.messages;

import com.synectiks.process.server.indexer.ElasticsearchException;

public class DocumentNotFoundException extends ElasticsearchException {
    public DocumentNotFoundException(String index, String messageId) {
        super("Couldn't find message <" + messageId + "> in index <" + index + ">");
    }
}
