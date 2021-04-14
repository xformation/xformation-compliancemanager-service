/*
 * */
package com.synectiks.process.server.system.debug;

import com.google.common.eventbus.EventBus;
import com.synectiks.process.server.system.debug.DebugEvent;
import com.synectiks.process.server.system.debug.DebugEventHolder;
import com.synectiks.process.server.system.debug.LocalDebugEventListener;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalDebugEventListenerTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Spy
    private EventBus serverEventBus;

    @Before
    public void setUp() {
        DebugEventHolder.setLocalDebugEvent(null);
        new LocalDebugEventListener(serverEventBus);
    }

    @Test
    public void testHandleDebugEvent() throws Exception {
        DebugEvent event = DebugEvent.create("Node ID", "Test");
        assertThat(DebugEventHolder.getLocalDebugEvent()).isNull();
        serverEventBus.post(event);
        assertThat(DebugEventHolder.getLocalDebugEvent()).isSameAs(event);
    }
}