/*
 * */
package com.synectiks.process.server.storage;

import com.synectiks.process.server.plugin.Version;

public class UnsupportedElasticsearchException extends RuntimeException {
    private final Version elasticsearchMajorVersion;

    public UnsupportedElasticsearchException(Version elasticsearchMajorVersion) {
        this.elasticsearchMajorVersion = elasticsearchMajorVersion;
    }

    public Version getElasticsearchMajorVersion() {
        return elasticsearchMajorVersion;
    }
}
