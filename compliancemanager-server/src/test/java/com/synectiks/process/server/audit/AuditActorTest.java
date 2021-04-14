/*
 * */
package com.synectiks.process.server.audit;

import org.junit.Test;

import com.synectiks.process.server.audit.AuditActor;
import com.synectiks.process.server.plugin.system.NodeId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuditActorTest {
    @Test
    public void testUser() throws Exception {
        final AuditActor actor = AuditActor.user("jane");

        assertThat(actor.urn()).isEqualTo("urn:graylog:user:jane");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyUser() throws Exception {
        AuditActor.user("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullUser() throws Exception {
        AuditActor.user(null);
    }

    @Test
    public void testSystem() throws Exception {
        final NodeId nodeId = mock(NodeId.class);
        when(nodeId.toString()).thenReturn("28164cbe-4ad9-4c9c-a76e-088655aa78892");
        final AuditActor actor = AuditActor.system(nodeId);

        assertThat(actor.urn()).isEqualTo("urn:graylog:node:28164cbe-4ad9-4c9c-a76e-088655aa78892");
    }

    @Test(expected = NullPointerException.class)
    public void testNullSystem() throws Exception {
        AuditActor.system(null);
    }
}