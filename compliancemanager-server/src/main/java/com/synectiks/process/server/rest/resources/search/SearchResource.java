/*
 * */
package com.synectiks.process.server.rest.resources.search;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.synectiks.process.server.decorators.DecoratorProcessor;
import com.synectiks.process.server.indexer.ranges.IndexRange;
import com.synectiks.process.server.indexer.results.ResultMessage;
import com.synectiks.process.server.indexer.results.ScrollResult;
import com.synectiks.process.server.indexer.results.SearchResult;
import com.synectiks.process.server.indexer.searches.Searches;
import com.synectiks.process.server.indexer.searches.SearchesClusterConfig;
import com.synectiks.process.server.indexer.searches.Sorting;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;
import com.synectiks.process.server.rest.models.messages.responses.ResultMessageSummary;
import com.synectiks.process.server.rest.models.system.indexer.responses.IndexRangeSummary;
import com.synectiks.process.server.rest.resources.search.responses.SearchResponse;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import org.glassfish.jersey.server.ChunkedOutput;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;

public abstract class SearchResource extends RestResource {
    private static final Logger LOG = LoggerFactory.getLogger(SearchResource.class);

    protected static final String DEFAULT_SCROLL_BATCH_SIZE = "500";

    protected final Searches searches;
    private final ClusterConfigService clusterConfigService;
    private final DecoratorProcessor decoratorProcessor;

    public SearchResource(Searches searches,
                          ClusterConfigService clusterConfigService,
                          DecoratorProcessor decoratorProcessor) {
        this.searches = searches;
        this.clusterConfigService = clusterConfigService;
        this.decoratorProcessor = decoratorProcessor;
    }

    protected List<String> parseFields(String fields) {
        if (isNullOrEmpty(fields)) {
            LOG.warn("Missing fields parameter. Returning HTTP 400");
            throw new BadRequestException("Missing required parameter `fields`");
        }
        return parseOptionalFields(fields);
    }

    protected List<String> parseOptionalFields(String fields) {
        if (isNullOrEmpty(fields)) {
            return null;
        }

        final Iterable<String> split = Splitter.on(',').omitEmptyStrings().trimResults().split(fields);
        final ArrayList<String> fieldList = Lists.newArrayList(Message.FIELD_TIMESTAMP);

        // skip the mandatory field timestamp
        for (String field : split) {
            if (Message.FIELD_TIMESTAMP.equals(field)) {
                continue;
            }
            fieldList.add(field);
        }

        return fieldList;
    }

    protected SearchResponse buildSearchResponse(SearchResult sr,
                                                 com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange timeRange,
                                                 boolean decorate,
                                                 Optional<String> streamId) {
        final SearchResponse result = SearchResponse.create(sr.getOriginalQuery(),
            sr.getBuiltQuery(),
            indexRangeListToValueList(sr.getUsedIndices()),
            resultMessageListtoValueList(sr.getResults()),
            sr.getFields(),
            sr.tookMs(),
            sr.getTotalResults(),
            timeRange.getFrom(),
            timeRange.getTo());

        return decorate ? decoratorProcessor.decorate(result, streamId) : result;
    }

    protected Set<IndexRangeSummary> indexRangeListToValueList(Set<IndexRange> indexRanges) {
        final Set<IndexRangeSummary> result = Sets.newHashSetWithExpectedSize(indexRanges.size());

        for (IndexRange indexRange : indexRanges) {
            result.add(IndexRangeSummary.create(
                indexRange.indexName(),
                indexRange.begin(),
                indexRange.end(),
                indexRange.calculatedAt(),
                indexRange.calculationDuration()));
        }

        return result;
    }

    protected List<ResultMessageSummary> resultMessageListtoValueList(List<ResultMessage> resultMessages) {
        return resultMessages.stream()
            // TODO module merge: migrate to resultMessage.getMessage() instead of Map<String, Object> via getFields()
            .map((resultMessage) -> ResultMessageSummary.create(resultMessage.highlightRanges, resultMessage.getMessage().getFields(), resultMessage.getIndex()))
            .collect(Collectors.toList());
    }

    protected Sorting buildSorting(String sort) {
        if (isNullOrEmpty(sort)) {
            return Sorting.DEFAULT;
        }

        try {
            return Sorting.fromApiParam(sort);
        } catch (Exception e) {
            LOG.error("Falling back to default sorting.", e);
            return Sorting.DEFAULT;
        }
    }

    protected ChunkedOutput<ScrollResult.ScrollChunk> buildChunkedOutput(final ScrollResult scroll) {
        final ChunkedOutput<ScrollResult.ScrollChunk> output = new ChunkedOutput<>(ScrollResult.ScrollChunk.class);

        LOG.debug("[{}] Scroll result contains a total of {} messages", scroll.getQueryHash(), scroll.totalHits());
        Runnable scrollIterationAction = createScrollChunkProducer(scroll, output);
        // TODO use a shared executor for async responses here instead of a single thread that's not limited
        new Thread(scrollIterationAction).start();
        return output;
    }

    public void checkSearchPermission(String filter, String searchPermission) {
        if (isNullOrEmpty(filter) || "*".equals(filter)) {
            checkPermission(searchPermission);
        } else {
            if (!filter.startsWith("streams:")) {
                throw new ForbiddenException("Not allowed to search with filter: [" + filter + "]");
            }

            String[] parts = filter.split(":");
            if (parts.length <= 1) {
                throw new ForbiddenException("Not allowed to search with filter: [" + filter + "]");
            }

            String streamList = parts[1];
            String[] streams = streamList.split(",");
            if (streams.length == 0) {
                throw new ForbiddenException("Not allowed to search with filter: [" + filter + "]");
            }

            for (String streamId : streams) {
                if (!isPermitted(RestPermissions.STREAMS_READ, streamId)) {
                    final String msg = "Not allowed to search with filter: [" + filter + "]. (Forbidden stream: " + streamId + ")";
                    LOG.warn(msg);
                    throw new ForbiddenException(msg);
                }
            }
        }
    }

    protected Runnable createScrollChunkProducer(final ScrollResult scroll,
                                                 final ChunkedOutput<ScrollResult.ScrollChunk> output) {
        return () -> {
            try {
                ScrollResult.ScrollChunk chunk = scroll.nextChunk();
                while (chunk != null) {
                    LOG.debug("[{}] Writing scroll chunk with {} messages",
                        scroll.getQueryHash(),
                        chunk.getMessages().size());
                    if (output.isClosed()) {
                        LOG.debug("[{}] Client connection is closed, client disconnected. Aborting scroll.",
                            scroll.getQueryHash());
                        scroll.cancel();
                        return;
                    }
                    output.write(chunk);
                    chunk = scroll.nextChunk();
                }
                LOG.debug("[{}] Reached end of scroll result.", scroll.getQueryHash());
                output.close();
            } catch (IOException e) {
                LOG.warn("[{}] Could not close chunked output stream for query scroll.", scroll.getQueryHash());
            }
        };
    }

    protected com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange restrictTimeRange(final com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange timeRange) {
        final DateTime originalFrom = timeRange.getFrom();
        final DateTime to = timeRange.getTo();
        final DateTime from;

        final SearchesClusterConfig config = clusterConfigService.get(SearchesClusterConfig.class);

        if (config == null || Period.ZERO.equals(config.queryTimeRangeLimit())) {
            from = originalFrom;
        } else {
            final DateTime limitedFrom = to.minus(config.queryTimeRangeLimit());
            from = limitedFrom.isAfter(originalFrom) ? limitedFrom : originalFrom;
        }

        return AbsoluteRange.create(from, to);
    }
}
