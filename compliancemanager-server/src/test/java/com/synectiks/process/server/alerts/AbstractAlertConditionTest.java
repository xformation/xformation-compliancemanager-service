/*
 * */
package com.synectiks.process.server.alerts;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.alerts.AbstractAlertCondition;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.alarms.AlertCondition;

import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

public class AbstractAlertConditionTest extends AlertConditionTest {
    @Test
    public void testDifferingTypesForNumericalParameters() throws Exception {
        final AlertCondition alertConditionWithDouble = getDummyAlertCondition(ImmutableMap.of("grace", 3.0));
        assertEquals(3, alertConditionWithDouble.getGrace());
        final AlertCondition alertConditionWithInteger = getDummyAlertCondition(ImmutableMap.of("grace", 3));
        assertEquals(3, alertConditionWithInteger.getGrace());
        final AlertCondition alertConditionWithStringDouble = getDummyAlertCondition(ImmutableMap.of("grace", "3.0"));
        assertEquals(3, alertConditionWithStringDouble.getGrace());
        final AlertCondition alertConditionWithStringInteger = getDummyAlertCondition(ImmutableMap.of("grace", "3"));
        assertEquals(3, alertConditionWithStringInteger.getGrace());
    }

    @Test
    public void testQueryFilterBuilder() {
        final AbstractAlertCondition condition = (AbstractAlertCondition) getDummyAlertCondition(ImmutableMap.of());

        assertThatThrownBy(() -> condition.buildQueryFilter(null, null))
                .hasMessageContaining("streamId")
                .hasMessageContaining("be null");
        assertThatThrownBy(() -> condition.buildQueryFilter("", null))
                .hasMessageContaining("streamId")
                .hasMessageContaining("be empty");

        assertThat(condition.buildQueryFilter("  abc123 ", null))
                .isEqualTo("streams:abc123");
        assertThat(condition.buildQueryFilter("abc123", ""))
                .isEqualTo("streams:abc123");
        assertThat(condition.buildQueryFilter("abc123", "*"))
                .isEqualTo("streams:abc123");
        assertThat(condition.buildQueryFilter("abc123", " *  "))
                .isEqualTo("streams:abc123");
        assertThat(condition.buildQueryFilter("abc123", " hello:world foo:\"bar baz\"   "))
                .isEqualTo("streams:abc123 AND (hello:world foo:\"bar baz\")");
        assertThat(condition.buildQueryFilter("abc123", "hello:world AND foo:\"bar baz\""))
                .isEqualTo("streams:abc123 AND (hello:world AND foo:\"bar baz\")");
        assertThat(condition.buildQueryFilter("abc123", "hello:world AND (foo:\"bar baz\" OR foo:yolo)"))
                .isEqualTo("streams:abc123 AND (hello:world AND (foo:\"bar baz\" OR foo:yolo))");
    }

    private AlertCondition getDummyAlertCondition(Map<String, Object> parameters) {
        return new AbstractAlertCondition(stream, CONDITION_ID, null, Tools.nowUTC(), STREAM_CREATOR, parameters, "Dummy Alert Condition") {
            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public AlertCondition.CheckResult runCheck() {
                return null;
            }
        };
    }
}
