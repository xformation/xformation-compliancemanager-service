/*
 * */
package com.synectiks.process.server.alerts;

import com.google.common.collect.Maps;
import com.synectiks.process.server.alerts.AbstractAlertCondition;
import com.synectiks.process.server.indexer.searches.Searches;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.alarms.AlertCondition;
import com.synectiks.process.server.plugin.streams.Stream;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public abstract class AlertConditionTest {
    protected static final String alertConditionTitle = "Alert Condition for Testing";

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    protected Stream stream;
    @Mock
    protected Searches searches;

    private final String STREAM_ID = "STREAMMOCKID";
    protected final String STREAM_CREATOR = "MOCKUSER";
    protected final String CONDITION_ID = "CONDITIONMOCKID";

    @Before
    public void setUp() throws Exception {
        when(stream.getId()).thenReturn(STREAM_ID);
    }

    protected void assertTriggered(AlertCondition alertCondition, AlertCondition.CheckResult result) {
        assertTrue("AlertCondition should be triggered, but it's not!", result.isTriggered());
        assertNotNull("Timestamp of returned check result should not be null!", result.getTriggeredAt());
        assertEquals("AlertCondition of result is not the same we created!", result.getTriggeredCondition(), alertCondition);
        long difference = Tools.nowUTC().getMillis() - result.getTriggeredAt().getMillis();
        assertTrue("AlertCondition should be triggered about now", difference < 1000);
    }

    protected void assertNotTriggered(AlertCondition.CheckResult result) {
        assertFalse("AlertCondition should not be triggered, but it is!", result.isTriggered());
        assertNull("No timestamp should be supplied if condition did not trigger", result.getTriggeredAt());
        assertNull("No triggered alert condition should be supplied if condition did not trigger", result.getTriggeredCondition());
    }

    protected Map<String, Object> getParametersMap(Integer grace, Integer time, Number threshold) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("grace", grace);
        parameters.put("time", time);
        parameters.put("threshold", threshold);
        return parameters;
    }

    protected <T extends AbstractAlertCondition> T getTestInstance(Class<T> klazz, Map<String, Object> parameters, String title) {
        try {
            return klazz.getConstructor(Searches.class, Stream.class, String.class, DateTime.class, String.class, Map.class, String.class)
                .newInstance(searches, stream, CONDITION_ID, Tools.nowUTC(), STREAM_CREATOR, parameters, title);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
