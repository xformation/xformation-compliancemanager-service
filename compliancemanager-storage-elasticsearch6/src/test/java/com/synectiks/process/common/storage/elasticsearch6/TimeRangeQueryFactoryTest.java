/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6;

import org.assertj.core.api.Assertions;
import org.graylog.shaded.elasticsearch5.org.elasticsearch.index.query.RangeQueryBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.synectiks.process.common.storage.elasticsearch6.TimeRangeQueryFactory;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeRangeQueryFactoryTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @BeforeClass
    public static void initialize() {
        DateTimeUtils.setCurrentMillisFixed(new DateTime(2016, 1, 1, 0, 0, DateTimeZone.UTC).getMillis());
    }

    @AfterClass
    public static void shutdown() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void getTimestampRangeFilterReturnsNullIfTimeRangeIsNull() {
        Assertions.assertThat(TimeRangeQueryFactory.create(null)).isNull();
    }

    @Test
    public void getTimestampRangeFilterReturnsRangeQueryWithGivenTimeRange() {
        final DateTime from = new DateTime(2016, 1, 15, 12, 0, DateTimeZone.UTC);
        final DateTime to = from.plusHours(1);
        final TimeRange timeRange = AbsoluteRange.create(from, to);
        final RangeQueryBuilder queryBuilder = (RangeQueryBuilder) TimeRangeQueryFactory.create(timeRange);
        assertThat(queryBuilder).isNotNull();
        assertThat(queryBuilder.fieldName()).isEqualTo("timestamp");
        assertThat(queryBuilder.from()).isEqualTo(Tools.buildElasticSearchTimeFormat(from));
        assertThat(queryBuilder.to()).isEqualTo(Tools.buildElasticSearchTimeFormat(to));
    }
}
