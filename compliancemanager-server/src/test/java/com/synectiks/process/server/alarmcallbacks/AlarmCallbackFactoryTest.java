/*
 * */
package com.synectiks.process.server.alarmcallbacks;

import com.google.inject.Injector;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackConfiguration;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackFactory;
import com.synectiks.process.server.plugin.alarms.callbacks.AlarmCallback;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AlarmCallbackFactoryTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private AlarmCallbackFactory alarmCallbackFactory;
    @Mock
    private Injector injector;
    @Mock
    private DummyAlarmCallback dummyAlarmCallback;

    public interface DummyAlarmCallback extends AlarmCallback {
    }

    @Before
    public void setUp() throws Exception {
        when(injector.getInstance(DummyAlarmCallback.class)).thenReturn(dummyAlarmCallback);
        Set<Class<? extends AlarmCallback>> availableAlarmCallbacks = new HashSet<Class<? extends AlarmCallback>>();
        availableAlarmCallbacks.add(DummyAlarmCallback.class);

        this.alarmCallbackFactory = new AlarmCallbackFactory(injector, availableAlarmCallbacks);
    }

    @Test
    public void testCreateByAlarmCallbackConfiguration() throws Exception {
        AlarmCallbackConfiguration configuration = mock(AlarmCallbackConfiguration.class);
        when(configuration.getType()).thenReturn(DummyAlarmCallback.class.getCanonicalName());

        AlarmCallback alarmCallback = alarmCallbackFactory.create(configuration);

        assertNotNull(alarmCallback);
        assertTrue(alarmCallback instanceof DummyAlarmCallback);
        assertEquals(dummyAlarmCallback, alarmCallback);
    }

    @Test
    public void testCreateByClassName() throws Exception {
        String className = DummyAlarmCallback.class.getCanonicalName();

        AlarmCallback alarmCallback = alarmCallbackFactory.create(className);

        assertNotNull(alarmCallback);
        assertTrue(alarmCallback instanceof DummyAlarmCallback);
        assertEquals(dummyAlarmCallback, alarmCallback);
    }

    @Test
    public void testCreateByClass() throws Exception {
        AlarmCallback alarmCallback = alarmCallbackFactory.create(DummyAlarmCallback.class);

        assertTrue(alarmCallback instanceof DummyAlarmCallback);
        assertEquals(dummyAlarmCallback, alarmCallback);
    }
}
