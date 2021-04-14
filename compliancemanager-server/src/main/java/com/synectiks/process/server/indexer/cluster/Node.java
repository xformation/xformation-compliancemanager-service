/*
 * */
package com.synectiks.process.server.indexer.cluster;

import com.github.zafarkhaja.semver.Version;
import com.synectiks.process.server.indexer.ElasticsearchException;

import javax.inject.Inject;
import java.util.Optional;

public class Node {
    private final NodeAdapter nodeAdapter;

    @Inject
    public Node(NodeAdapter nodeAdapter) {
        this.nodeAdapter = nodeAdapter;
    }

    public Optional<Version> getVersion() {
        return nodeAdapter.version()
            .map(this::parseVersion);
    }

    private Version parseVersion(String version) {
        try {
            return Version.valueOf(version);
        } catch (Exception e) {
            throw new ElasticsearchException("Unable to parse Elasticsearch version: " + version, e);
        }
    }
}
