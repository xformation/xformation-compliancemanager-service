/*
 * */
package com.synectiks.process.server.periodical;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.indexer.IndexSet;
import com.synectiks.process.server.indexer.IndexSetRegistry;
import com.synectiks.process.server.indexer.NoTargetIndexException;
import com.synectiks.process.server.indexer.cluster.Cluster;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.indexer.indices.Indices;
import com.synectiks.process.server.notifications.NotificationService;
import com.synectiks.process.server.periodical.IndexRotationThread;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategy;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategyConfig;
import com.synectiks.process.server.plugin.system.NodeId;
import com.synectiks.process.server.shared.system.activities.NullActivityWriter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.inject.Provider;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IndexRotationThreadTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private IndexSet indexSet;
    @Mock
    private IndexSetConfig indexSetConfig;
    @Mock
    private NotificationService notificationService;
    @Mock
    private Indices indices;
    @Mock
    private Cluster cluster;
    @Mock
    private NodeId nodeId;
    @Mock
    private IndexSetRegistry indexSetRegistry;

    @Before
    public void setUp() throws Exception {
        when(indexSet.getConfig()).thenReturn(indexSetConfig);
    }

    @Test
    public void testPerformRotation() throws NoTargetIndexException {
        final Provider<RotationStrategy> provider = new RotationStrategyProvider() {
            @Override
            public void doRotate(IndexSet indexSet) {
                indexSet.cycle();
            }
        };

        final IndexRotationThread rotationThread = new IndexRotationThread(
                notificationService,
                indices,
                indexSetRegistry,
                cluster,
                new NullActivityWriter(),
                nodeId,
                ImmutableMap.<String, Provider<RotationStrategy>>builder().put("strategy", provider).build()
        );
        when(indexSetConfig.rotationStrategyClass()).thenReturn("strategy");

        rotationThread.checkForRotation(indexSet);

        verify(indexSet, times(1)).cycle();
    }

    @Test
    public void testDoNotPerformRotation() throws NoTargetIndexException {
        final Provider<RotationStrategy> provider = new RotationStrategyProvider();

        final IndexRotationThread rotationThread = new IndexRotationThread(
                notificationService,
                indices,
                indexSetRegistry,
                cluster,
                new NullActivityWriter(),
                nodeId,
                ImmutableMap.<String, Provider<RotationStrategy>>builder().put("strategy", provider).build()
        );
        when(indexSetConfig.rotationStrategyClass()).thenReturn("strategy");

        rotationThread.checkForRotation(indexSet);

        verify(indexSet, never()).cycle();
    }

    @Test
    public void testDoNotPerformRotationIfClusterIsDown() throws NoTargetIndexException {
        final Provider<RotationStrategy> provider = spy(new RotationStrategyProvider());
        when(cluster.isConnected()).thenReturn(false);

        final IndexRotationThread rotationThread = new IndexRotationThread(
                notificationService,
                indices,
                indexSetRegistry,
                cluster,
                new NullActivityWriter(),
                nodeId,
                ImmutableMap.<String, Provider<RotationStrategy>>builder().put("strategy", provider).build()
        );
        rotationThread.doRun();

        verify(indexSet, never()).cycle();
        verify(provider, never()).get();
    }

    private static class RotationStrategyProvider implements Provider<RotationStrategy> {
        @Override
        public RotationStrategy get() {
            return new RotationStrategy() {
                @Override
                public void rotate(IndexSet indexSet) {
                    doRotate(indexSet);
                }

                @Override
                public RotationStrategyConfig defaultConfiguration() {
                    return null;
                }

                @Override
                public Class<? extends RotationStrategyConfig> configurationClass() {
                    return null;
                }
            };
        }

        public void doRotate(IndexSet indexSet) {
        }
    }
}
