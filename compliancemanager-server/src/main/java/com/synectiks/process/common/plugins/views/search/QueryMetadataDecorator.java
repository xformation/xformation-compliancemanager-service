/*
 * */
package com.synectiks.process.common.plugins.views.search;

public interface QueryMetadataDecorator {
    QueryMetadata decorate(Search search, Query query, QueryMetadata queryMetadata);
}
