/*
 * */
package com.synectiks.process.common.testing.completebackend;

import org.testcontainers.containers.Network;

import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;

public interface ElasticsearchInstanceFactory {
    ElasticsearchInstance create(Network network);

    String version();
}
