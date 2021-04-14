/*
 * */
package com.synectiks.process.server.storage.versionprobe;

public class ElasticsearchProbeException extends RuntimeException {
    public ElasticsearchProbeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ElasticsearchProbeException(String message) {
        super(message);
    }
}
