/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivots.buckets;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.Pivot;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.buckets.DateInterval;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.buckets.Interval;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.buckets.Time;
import com.synectiks.process.common.plugins.views.search.timeranges.DerivedTimeRange;
import com.synectiks.process.common.storage.elasticsearch6.views.ESGeneratedQueryContext;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot.ESPivot;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot.buckets.ESTimeHandler;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.InvalidRangeParametersException;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.RelativeRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ESTimeHandlerTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private ESTimeHandler esTimeHandler;

    @Mock
    private Pivot pivot;

    @Mock
    private Time time;

    @Mock
    private ESPivot esPivot;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ESGeneratedQueryContext queryContext;

    @Mock
    private Query query;

    @Mock
    private Interval interval;

    @Before
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
}
