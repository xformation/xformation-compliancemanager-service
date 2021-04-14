/*
 * */
package com.synectiks.process.server.system.debug;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.synectiks.process.server.events.ClusterEventBus;
import com.synectiks.process.server.system.debug.ClusterDebugEventListener;
import com.synectiks.process.server.system.debug.DebugEvent;
import com.synectiks.process.server.system.debug.DebugEventHolder;

import static org.assertj.core.api.Assertions.assertThat;

public class ClusterDebugEventListenerTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Spy
    private ClusterEventBus clusterEventBus;

    @Before
    public void setUp() {
        DebugEventHolder.setClusterDebugEvent(null);
        new ClusterDebugEventListener(clusterEventBus);
    }

    @Test
    public void testHandleDebugEvent() throws Exception {
        DebugEvent event = DebugEvent.create("Node ID", "Test");
        assertThat(DebugEventHolder.getClusterDebugEvent()).isNull();
        clusterEventBus.post(event);
        assertThat(DebugEventHolder.getClusterDebugEvent()).isSameAs(event);
    }
}