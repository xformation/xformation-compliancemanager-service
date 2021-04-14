/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch7.views;

import org.graylog.shaded.elasticsearch7.org.elasticsearch.action.search.SearchRequest;

import java.util.List;
import java.util.stream.Collectors;

class ViewsUtils {
    static List<String> indicesOf(List<SearchRequest> clientRequest) {
        return clientRequest.stream()
                .map(request -> String.join(",", request.indices()))
                .collect(Collectors.toList());
    }
}
