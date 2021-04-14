/*
 * */
package com.synectiks.process.server.indexer;

public class NoTargetIndexException extends ElasticsearchException {
    public NoTargetIndexException(String message) {
        super(message);
    }
}
