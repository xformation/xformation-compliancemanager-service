/*
 * */
package com.synectiks.process.server.indexer.rotation.strategies;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.synectiks.process.server.audit.AuditEventSender;
import com.synectiks.process.server.indexer.IndexNotFoundException;
import com.synectiks.process.server.indexer.IndexSet;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.indexer.indices.Indices;
import com.synectiks.process.server.indexer.rotation.strategies.MessageCountRotationStrategy;
import com.synectiks.process.server.indexer.rotation.strategies.MessageCountRotationStrategyConfig;
import com.synectiks.process.server.plugin.system.NodeId;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessageCountRotationStrategyTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private IndexSet indexSet;

    @Mock
    private IndexSetConfig indexSetConfig;

    @Mock
    private Indices indices;

    @Mock
    private NodeId nodeId;

    @Mock
    private AuditEventSender auditEventSender;

    @Test
    public void testRotate() throws Exception {
        when(indices.numberOfMessages("name")).thenReturn(10L);
        when(indexSet.getNewestIndex()).thenReturn("name");
        when(indexSet.getConfig()).thenReturn(indexSetConfig);
        when(indexSetConfig.rotationStrategy()).thenReturn(MessageCountRotationStrategyConfig.create(5));

        final MessageCountRotationStrategy strategy = new MessageCountRotationStrategy(indices, nodeId, auditEventSender);

        strategy.rotate(indexSet);
        verify(indexSet, times(1)).cycle();
        reset(indexSet);
    }

    @Test
    public void testDontRotate() throws Exception {
        when(indices.numberOfMessages("name")).thenReturn(1L);
        when(indexSet.getNewestIndex()).thenReturn("name");
        when(indexSet.getConfig()).thenReturn(indexSetConfig);
        when(indexSetConfig.rotationStrategy()).thenReturn(MessageCountRotationStrategyConfig.create(5));

        final MessageCountRotationStrategy strategy = new MessageCountRotationStrategy(indices, nodeId, auditEventSender);

        strategy.rotate(indexSet);
        verify(indexSet, never()).cycle();
        reset(indexSet);
    }


    @Test
    public void testIndexUnavailable() throws Exception {
        doThrow(IndexNotFoundException.class).when(indices).numberOfMessages("name");
        when(indexSet.getNewestIndex()).thenReturn("name");
        when(indexSet.getConfig()).thenReturn(indexSetConfig);
        when(indexSetConfig.rotationStrategy()).thenReturn(MessageCountRotationStrategyConfig.create(5));

        final MessageCountRotationStrategy strategy = new MessageCountRotationStrategy(indices, nodeId, auditEventSender);

        strategy.rotate(indexSet);
        verify(indexSet, never()).cycle();
        reset(indexSet);
    }
}
