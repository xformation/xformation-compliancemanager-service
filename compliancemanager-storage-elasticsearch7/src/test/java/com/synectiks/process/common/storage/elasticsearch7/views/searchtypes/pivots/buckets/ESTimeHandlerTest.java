/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch7.views.searchtypes.pivots.buckets;

import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.Pivot;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.buckets.DateInterval;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.buckets.Interval;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.buckets.Time;
import com.synectiks.process.common.plugins.views.search.timeranges.DerivedTimeRange;
import com.synectiks.process.common.storage.elasticsearch7.views.ESGeneratedQueryContext;
import com.synectiks.process.common.storage.elasticsearch7.views.searchtypes.pivot.ESPivot;
import com.synectiks.process.common.storage.elasticsearch7.views.searchtypes.pivot.buckets.ESTimeHandler;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.InvalidRangeParametersException;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.RelativeRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ESTimeHandlerTest {
    private ESTimeHandler esTimeHandler;

    private final Pivot pivot = mock(Pivot.class);

    private final Time time = mock(Time.class);

    private final ESPivot esPivot = mock(ESPivot.class);

    private final ESGeneratedQueryContext queryContext = mock(ESGeneratedQueryContext.class, RETURNS_DEEP_STUBS);

    private final Query query = mock(Query.class);

    private final Interval interval = mock(Interval.class, RETURNS_DEEP_STUBS);

    @BeforeEach
    public void setUp() throws Exception {
        this.esTimeHandler = new ESTimeHandler();
        when(time.interval()).thenReturn(interval);
        when(time.field()).thenReturn("foobar");
        final ESPivot.AggTypes aggTypes = mock(ESPivot.AggTypes.class);
        when(queryContext.contextMap().get(any())).thenReturn(aggTypes);
        when(query.effectiveTimeRange(any())).thenCallRealMethod();
    }

    @Test
    public void timeSpecIntervalIsCalculatedOnPivotTimerangeIfOverridden() throws InvalidRangeParametersException {
        final ArgumentCaptor<TimeRange> timeRangeCaptor = ArgumentCaptor.forClass(TimeRange.class);
        when(interval.toDateInterval(timeRangeCaptor.capture())).thenReturn(DateInterval.days(1));
        when(pivot.timerange()).thenReturn(Optional.of(DerivedTimeRange.of(RelativeRange.create(4242))));

        this.esTimeHandler.doCreateAggregation("foobar", pivot, time, esPivot, queryContext, query);

        final TimeRange argumentTimeRange = timeRangeCaptor.getValue();
        assertThat(argumentTimeRange).isEqualTo(RelativeRange.create(4242));
    }

    @Test
    public void timeSpecIntervalIsCalculatedOnQueryTimeRangeIfNoPivotTimeRange() throws InvalidRangeParametersException {
        final ArgumentCaptor<TimeRange> timeRangeCaptor = ArgumentCaptor.forClass(TimeRange.class);
        when(interval.toDateInterval(timeRangeCaptor.capture())).thenReturn(DateInterval.days(1));
        when(pivot.timerange()).thenReturn(Optional.empty());
        when(query.timerange()).thenReturn(RelativeRange.create(2323));

        this.esTimeHandler.doCreateAggregation("foobar", pivot, time, esPivot, queryContext, query);

        final TimeRange argumentTimeRange = timeRangeCaptor.getValue();
        assertThat(argumentTimeRange).isEqualTo(RelativeRange.create(2323));
    }


    @ParameterizedTest
    @ValueSource(strings = { "1s", "1M", "4s", "14d" })
    public void correctIntervalTypeIsUsedForAggregation(String intervalString) throws InvalidRangeParametersException {
        when(pivot.timerange()).thenReturn(Optional.empty());
        when(query.timerange()).thenReturn(RelativeRange.create(2323));
        when(interval.toDateInterval(any(TimeRange.class)).toString()).thenReturn(intervalString);

        this.esTimeHandler.doCreateAggregation("foobar", pivot, time, esPivot, queryContext, query);
    }
}
