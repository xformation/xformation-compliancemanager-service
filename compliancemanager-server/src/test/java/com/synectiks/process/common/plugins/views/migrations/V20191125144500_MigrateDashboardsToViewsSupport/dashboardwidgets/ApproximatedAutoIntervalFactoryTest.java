/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.dashboardwidgets;

import org.joda.time.DateTime;
import org.junit.Test;

import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.AbsoluteRange;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.KeywordRange;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.RelativeRange;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.dashboardwidgets.ApproximatedAutoIntervalFactory;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets.AutoInterval;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets.Interval;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets.TimeUnitInterval;

import static org.assertj.core.api.Assertions.assertThat;

public class ApproximatedAutoIntervalFactoryTest {
    @Test
    public void returnsParsedIntervalIfKeywordRange() {
        final KeywordRange keywordRange = KeywordRange.create("yesterday");

        final Interval interval = ApproximatedAutoIntervalFactory.of("minute", keywordRange);

        assertThat(interval).isEqualTo(TimeUnitInterval.create(TimeUnitInterval.IntervalUnit.MINUTES, 1));
    }

    @Test
    public void approximatesAutoIntervalWithScalingIfRelativeRangeAndBeyondLimits() {
        final RelativeRange relativeRange = RelativeRange.create(7200);

        final Interval interval = ApproximatedAutoIntervalFactory.of("minute", relativeRange);

        assertThat(interval).isEqualTo(AutoInterval.create(2.0));
    }

    @Test
    public void returnsParsedIntervalIfRelativeRangeButBelowLimit() {
        final RelativeRange relativeRange = RelativeRange.create(450);

        final Interval interval = ApproximatedAutoIntervalFactory.of("minute", relativeRange);

        assertThat(interval).isEqualTo(TimeUnitInterval.create(TimeUnitInterval.IntervalUnit.MINUTES, 1));
    }

    @Test
    public void returnsParsedIntervalIfRelativeRangeButAboveLimit() {
        final RelativeRange relativeRange = RelativeRange.create(86400);

        final Interval interval = ApproximatedAutoIntervalFactory.of("minute", relativeRange);

        assertThat(interval).isEqualTo(TimeUnitInterval.create(TimeUnitInterval.IntervalUnit.MINUTES, 1));
    }
    @Test
    public void approximatesAutoIntervalWithScalingIfAbsoluteRangeAndBeyondLimits() {
        final AbsoluteRange absoluteRange = AbsoluteRange.create(
                DateTime.parse("2019-12-02T12:50:23Z"),
                DateTime.parse("2019-12-02T14:50:23Z")
        );

        final Interval interval = ApproximatedAutoIntervalFactory.of("minute", absoluteRange);

        assertThat(interval).isEqualTo(AutoInterval.create(2.0));
    }

    @Test
    public void returnsParsedIntervalIfAbsoluteRangeButBelowLimit() {
        final AbsoluteRange absoluteRange = AbsoluteRange.create(
                DateTime.parse("2019-12-02T14:42:53Z"),
                DateTime.parse("2019-12-02T14:50:23Z")
        );

        final Interval interval = ApproximatedAutoIntervalFactory.of("minute", absoluteRange);

        assertThat(interval).isEqualTo(TimeUnitInterval.create(TimeUnitInterval.IntervalUnit.MINUTES, 1));
    }

    @Test
    public void returnsParsedIntervalIfAbsoluteRangeButAboveLimit() {
        final AbsoluteRange absoluteRange = AbsoluteRange.create(
                DateTime.parse("2019-12-01T14:50:23Z"),
                DateTime.parse("2019-12-02T14:50:23Z")
        );

        final Interval interval = ApproximatedAutoIntervalFactory.of("minute", absoluteRange);

        assertThat(interval).isEqualTo(TimeUnitInterval.create(TimeUnitInterval.IntervalUnit.MINUTES, 1));
    }
}
