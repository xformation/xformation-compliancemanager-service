/*
 * */
package com.synectiks.process.server.alerts.types;

import org.junit.Test;

import com.synectiks.process.server.alerts.AlertConditionTest;
import com.synectiks.process.server.alerts.types.MessageCountAlertCondition;
import com.synectiks.process.server.indexer.results.CountResult;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.alarms.AlertCondition;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageCountAlertConditionTest extends AlertConditionTest {
    private final int threshold = 100;

    @Test
    public void testConstructor() throws Exception {
        final Map<String, Object> parameters = getParametersMap(0, 0, MessageCountAlertCondition.ThresholdType.MORE, 0);

        final MessageCountAlertCondition messageCountAlertCondition = getMessageCountAlertCondition(parameters, alertConditionTitle);

        assertNotNull(messageCountAlertCondition);
        assertNotNull(messageCountAlertCondition.getDescription());
        final String thresholdType = (String) messageCountAlertCondition.getParameters().get("threshold_type");
        assertEquals(thresholdType, thresholdType.toUpperCase(Locale.ENGLISH));
    }

    /*
     * Ensure MessageCountAlertCondition objects created before 2.2.0 and having a lowercase threshold_type,
     * get converted to uppercase for consistency with new created alert conditions.
     */
    @Test
    public void testConstructorOldObjects() throws Exception {
        final Map<String, Object> parameters = getParametersMap(0, 0, MessageCountAlertCondition.ThresholdType.MORE, 0);
        parameters.put("threshold_type", MessageCountAlertCondition.ThresholdType.MORE.toString().toLowerCase(Locale.ENGLISH));

        final MessageCountAlertCondition messageCountAlertCondition = getMessageCountAlertCondition(parameters, alertConditionTitle);

        final String thresholdType = (String) messageCountAlertCondition.getParameters().get("threshold_type");
        assertEquals(thresholdType, thresholdType.toUpperCase(Locale.ENGLISH));
    }

    @Test
    public void testRunCheckMorePositive() throws Exception {
        final MessageCountAlertCondition.ThresholdType type = MessageCountAlertCondition.ThresholdType.MORE;

        final MessageCountAlertCondition messageCountAlertCondition = getConditionWithParameters(type, threshold);

        searchCountShouldReturn(threshold + 1);
        // AlertCondition was never triggered before
        final AlertCondition.CheckResult result = messageCountAlertCondition.runCheck();

        assertTriggered(messageCountAlertCondition, result);
    }

    @Test
    public void testRunCheckLessPositive() throws Exception {
        final MessageCountAlertCondition.ThresholdType type = MessageCountAlertCondition.ThresholdType.LESS;

        final MessageCountAlertCondition messageCountAlertCondition = getConditionWithParameters(type, threshold);

        searchCountShouldReturn(threshold - 1);

        final AlertCondition.CheckResult result = messageCountAlertCondition.runCheck();

        assertTriggered(messageCountAlertCondition, result);
    }

    @Test
    public void testRunCheckMoreNegative() throws Exception {
        final MessageCountAlertCondition.ThresholdType type = MessageCountAlertCondition.ThresholdType.MORE;

        final MessageCountAlertCondition messageCountAlertCondition = getConditionWithParameters(type, threshold);

        searchCountShouldReturn(threshold);

        final AlertCondition.CheckResult result = messageCountAlertCondition.runCheck();

        assertNotTriggered(result);
    }

    @Test
    public void testRunCheckLessNegative() throws Exception {
        final MessageCountAlertCondition.ThresholdType type = MessageCountAlertCondition.ThresholdType.LESS;

        final MessageCountAlertCondition messageCountAlertCondition = getConditionWithParameters(type, threshold);

        searchCountShouldReturn(threshold);

        final AlertCondition.CheckResult result = messageCountAlertCondition.runCheck();

        assertNotTriggered(result);
    }

    private MessageCountAlertCondition getConditionWithParameters(MessageCountAlertCondition.ThresholdType type, Integer threshold) {
        Map<String, Object> parameters = simplestParameterMap(type, threshold);
        return getMessageCountAlertCondition(parameters, alertConditionTitle);
    }

    private Map<String, Object> simplestParameterMap(MessageCountAlertCondition.ThresholdType type, Integer threshold) {
        return getParametersMap(0, 0, type, threshold);
    }

    private void searchCountShouldReturn(long count) {
        final CountResult countResult = mock(CountResult.class);
        when(countResult.count()).thenReturn(count);

        when(searches.count(anyString(), any(TimeRange.class), anyString())).thenReturn(countResult);
    }

    private MessageCountAlertCondition getMessageCountAlertCondition(Map<String, Object> parameters, String title) {
        return new MessageCountAlertCondition(
            searches,
            stream,
            CONDITION_ID,
            Tools.nowUTC(),
            STREAM_CREATOR,
            parameters,
            title);
    }

    private Map<String, Object> getParametersMap(Integer grace, Integer time, MessageCountAlertCondition.ThresholdType type, Number threshold) {
        Map<String, Object> parameters = super.getParametersMap(grace, time, threshold);
        parameters.put("threshold_type", type.toString());

        return parameters;
    }
}
