/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.SeriesSpec;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets.Series;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SeriesTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "simple",
            "mixedCasING",
            "with_underscore",
            "_leadingunderscore",
            "trailingunderscore_",
            "with-dash",
            "-leadingdash",
            "trailingdash-",
            "with@at",
            "@leadingat",
            "trailingat_",
            "-@_"
    })
    void canDestructureAllValidFieldNames(String fieldName) {

        Series sut = Series.builder().function("avg(" + fieldName + ")").build();

        SeriesSpec seriesSpec = sut.toSeriesSpec();

        assertThat(seriesSpec.field()).contains(fieldName);
    }
}
