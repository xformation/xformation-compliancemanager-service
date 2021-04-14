/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6;

import org.graylog.shaded.elasticsearch5.org.elasticsearch.index.query.QueryBuilders;
import org.graylog.shaded.elasticsearch5.org.elasticsearch.index.query.RangeQueryBuilder;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import javax.annotation.Nullable;

public class TimeRangeQueryFactory {
    @Nullable
    public static RangeQueryBuilder create(TimeRange range) {
        if (range == null) {
            return null;
        }

        return QueryBuilders.rangeQuery(Message.FIELD_TIMESTAMP)
                .gte(Tools.buildElasticSearchTimeFormat(range.getFrom()))
                .lte(Tools.buildElasticSearchTimeFormat(range.getTo()));
    }
}
