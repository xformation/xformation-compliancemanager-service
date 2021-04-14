/*
 * */
package com.synectiks.process.server.system.debug;

import org.junit.Test;

import com.synectiks.process.server.system.debug.DebugEvent;
import com.synectiks.process.server.system.debug.DebugEventHolder;

import static org.assertj.core.api.Assertions.assertThat;

public class DebugEventHolderTest {
    @Test
    public void setAndGetClusterEventReturnsSameObject() throws Exception {
        DebugEvent event = DebugEvent.create("Node ID", "Test");
        DebugEventHolder.setClusterDebugEvent(event);

        assertThat(DebugEventHolder.getClusterDebugEvent()).isSameAs(event);
    }

    @Test
    public void setAndGetDebugEventReturnsSameObject() throws Exception {
        DebugEvent event = DebugEvent.create("Node ID", "Test");
        DebugEventHolder.setLocalDebugEvent(event);

        assertThat(DebugEventHolder.getLocalDebugEvent()).isSameAs(event);
    }
}