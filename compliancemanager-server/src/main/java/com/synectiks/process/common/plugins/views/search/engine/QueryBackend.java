/*
 * */
package com.synectiks.process.common.plugins.views.search.engine;

import com.google.common.base.Stopwatch;
import com.synectiks.process.common.plugins.views.search.GlobalOverride;
import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.QueryResult;
import com.synectiks.process.common.plugins.views.search.SearchJob;
import com.synectiks.process.common.plugins.views.search.errors.QueryError;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.PivotResult;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.RelativeRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import org.joda.time.DateTime;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A search backend that is capable of generating and executing search jobs
 *
 * @param <T> the type of the generated query
 */
public interface QueryBackend<T extends GeneratedQueryContext> {

    /**
     * Generate a backend-specific query out of the logical query structure.
     *
     * @param job                currently executing job
     * @param query              the compliancemanager query structure
     * @param predecessorResults the query result of the preceding queries
     * @return a backend specific generated query
     */
    T generate(SearchJob job, Query query, Set<QueryResult> predecessorResults);

    default boolean isAllMessages(TimeRange timeRange) {
        return timeRange instanceof RelativeRange && ((RelativeRange)timeRange).isAllMessages();
    }

    default AbsoluteRange effectiveTimeRangeForResult(Query query, QueryResult queryResult) {
        final TimeRange effectiveTimeRange = query.globalOverride().flatMap(GlobalOverride::timerange).orElse(query.timerange());

        if (isAllMessages(effectiveTimeRange)) {
            final Optional<AbsoluteRange> effectiveRange = queryResult.searchTypes().values().stream()
                    .filter(result -> result instanceof PivotResult)
                    .map(result -> ((PivotResult) result).effectiveTimerange())
                    .reduce((prev, next) -> {
                        final DateTime from = prev.from().compareTo(next.from()) < 0 ? prev.from() : next.from();
                        final DateTime to = prev.to().compareTo(next.to()) < 0 ? next.to() : prev.to();
                        return AbsoluteRange.create(from, to);
                    });

            if (effectiveRange.isPresent()) {
                return effectiveRange.get();
            }
        }
        return AbsoluteRange.create(effectiveTimeRange.getFrom(), effectiveTimeRange.getTo());
    }

    // TODO we can probably push job, query and predecessorResults into the GeneratedQueryContext to simplify the signature
    default QueryResult run(SearchJob job, Query query, GeneratedQueryContext generatedQueryContext, Set<QueryResult> predecessorResults) {
        try {
            final Stopwatch stopwatch = Stopwatch.createStarted();
            final QueryExecutionStats.Builder statsBuilder = QueryExecutionStats.builderWithCurrentTime();
            // https://www.ibm.com/developerworks/java/library/j-jtp04298/index.html#3.0
            //noinspection unchecked
            final QueryResult result = doRun(job, query, (T) generatedQueryContext, predecessorResults);
            stopwatch.stop();
            return result.toBuilder()
                    .executionStats(
                            statsBuilder.duration(stopwatch.elapsed(TimeUnit.MILLISECONDS))
                                    .effectiveTimeRange(effectiveTimeRangeForResult(query, result))
                                    .build())
                    .build();
        } catch (Exception e) {
            // the backend has very likely created a more specific error and added it to the context, but we fall
            // back to a generic error so we never throw exceptions into the engine.
            final QueryError queryError = new QueryError(query, e);
            generatedQueryContext.addError(queryError);
            return QueryResult.failedQueryWithError(query, queryError);
        }
    }

    /**
     * Run the generated query as part of the given query job.
     * <p>
     * This method is typically being run in an executor and can safely block.
     *
     * @param job                currently executing job
     * @param query              the individual query to run from the current job
     * @param queryContext       the generated query by {@link #generate(SearchJob, Query, Set)}
     * @param predecessorResults the query result of the preceding queries
     * @return the result for the query
     * @throws RuntimeException if the query could not be executed for some reason
     */
    QueryResult doRun(SearchJob job, Query query, T queryContext, Set<QueryResult> predecessorResults);
}
