/*
 * */
package com.synectiks.process.server.indexer;

import com.github.zafarkhaja.semver.Version;
import com.synectiks.process.server.indexer.cluster.Node;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class IndexMappingFactory {
    private final Node node;

    @Inject
    public IndexMappingFactory(Node node) {
        this.node = node;
    }

    public IndexMappingTemplate createIndexMapping(IndexSetConfig.TemplateType templateType) {
        final Version elasticsearchVersion = node.getVersion().orElseThrow(() -> new ElasticsearchException("Unable to retrieve Elasticsearch version."));

        switch (templateType) {
            case MESSAGES: return indexMappingFor(elasticsearchVersion);
            case EVENTS: return eventsIndexMappingFor(elasticsearchVersion);
            case GIM_V1: return gimMappingFor(elasticsearchVersion);
            default: throw new IllegalStateException("Invalid index template type: " + templateType);
        }
    }

    private IndexMapping gimMappingFor(Version elasticsearchVersion) {
        if (elasticsearchVersion.satisfies("^6.0.0")) {
            return new GIMMapping6();
        } else if (elasticsearchVersion.satisfies("^7.0.0")) {
            return new GIMMapping7();
        } else {
            throw new ElasticsearchException("Unsupported Elasticsearch version: " + elasticsearchVersion);
        }
    }

    public static IndexMapping indexMappingFor(Version elasticsearchVersion) {
        if (elasticsearchVersion.satisfies("^5.0.0")) {
            return new IndexMapping5();
        } else if (elasticsearchVersion.satisfies("^6.0.0")) {
            return new IndexMapping6();
        } else if (elasticsearchVersion.satisfies("^7.0.0")) {
            return new IndexMapping7();
        } else {
            throw new ElasticsearchException("Unsupported Elasticsearch version: " + elasticsearchVersion);
        }
    }

    public static IndexMappingTemplate eventsIndexMappingFor(Version elasticsearchVersion) {
        if (elasticsearchVersion.satisfies("^5.0.0 | ^6.0.0")) {
            return new EventsIndexMapping6();
        } else if (elasticsearchVersion.satisfies("^7.0.0")) {
            return new EventsIndexMapping7();
        } else {
            throw new ElasticsearchException("Unsupported Elasticsearch version: " + elasticsearchVersion);
        }
    }
}
