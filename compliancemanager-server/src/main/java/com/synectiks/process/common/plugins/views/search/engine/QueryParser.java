/*
 * */
package com.synectiks.process.common.plugins.views.search.engine;

import com.google.common.collect.Sets;
import com.google.common.graph.Traverser;
import com.synectiks.process.common.plugins.views.search.Filter;
import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.QueryMetadata;
import com.synectiks.process.common.plugins.views.search.SearchType;
import com.synectiks.process.common.plugins.views.search.elasticsearch.ElasticsearchQueryString;
import com.synectiks.process.common.plugins.views.search.elasticsearch.QueryStringParser;
import com.synectiks.process.common.plugins.views.search.filter.QueryStringFilter;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;

public class QueryParser {
    private final QueryStringParser queryStringParser;

    @Inject
    public QueryParser(QueryStringParser queryStringParser) {
        this.queryStringParser = queryStringParser;
    }

    public QueryMetadata parse(Query query) {
        checkArgument(query.query() instanceof ElasticsearchQueryString);
        final String mainQueryString = ((ElasticsearchQueryString) query.query()).queryString();
        final java.util.stream.Stream<String> queryStringStreams = java.util.stream.Stream.concat(
                java.util.stream.Stream.of(mainQueryString),
                query.searchTypes().stream().flatMap(this::queryStringsFromSearchType)
        );

        return queryStringStreams
                .map(queryStringParser::parse)
                .reduce(QueryMetadata.builder().build(), (meta1, meta2) -> QueryMetadata.builder().usedParameterNames(
                        Sets.union(meta1.usedParameterNames(), meta2.usedParameterNames())
                ).build());
    }


    private java.util.stream.Stream<String> queryStringsFromSearchType(SearchType searchType) {
        return java.util.stream.Stream.concat(
                searchType.query().filter(query -> query instanceof ElasticsearchQueryString)
                        .map(query -> ((ElasticsearchQueryString) query).queryString())
                        .map(java.util.stream.Stream::of)
                        .orElse(java.util.stream.Stream.empty()),
                queryStringsFromFilter(searchType.filter()).stream()
        );
    }

    private Set<String> queryStringsFromFilter(Filter entry) {
        if (entry != null) {
            final Traverser<Filter> filterTraverser = Traverser.forTree(filter -> firstNonNull(filter.filters(), Collections.emptySet()));
            return StreamSupport.stream(filterTraverser.breadthFirst(entry).spliterator(), false)
                    .filter(filter -> filter instanceof QueryStringFilter)
                    .map(queryStringFilter -> ((QueryStringFilter) queryStringFilter).query())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }
}
