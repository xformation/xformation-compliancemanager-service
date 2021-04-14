/*
 * */
package com.synectiks.process.server.indexer.retention.strategies;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.synectiks.process.server.indexer.IndexSet;
import com.synectiks.process.server.indexer.indices.Indices;
import com.synectiks.process.server.indexer.retention.strategies.AbstractIndexCountBasedRetentionStrategy;
import com.synectiks.process.server.plugin.indexer.retention.RetentionStrategyConfig;
import com.synectiks.process.server.shared.system.activities.Activity;
import com.synectiks.process.server.shared.system.activities.ActivityWriter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AbstractIndexCountBasedRetentionStrategyTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private AbstractIndexCountBasedRetentionStrategy retentionStrategy;
    @Mock
    private Indices indices;
    @Mock
    private ActivityWriter activityWriter;
    @Mock
    private IndexSet indexSet;

    private Map<String, Set<String>> indexMap;

    @Before
    public void setUp() throws Exception {
        indexMap = new HashMap<>();
        indexMap.put("index1", Collections.emptySet());
        indexMap.put("index2", Collections.emptySet());
        indexMap.put("index3", Collections.emptySet());
        indexMap.put("index4", Collections.emptySet());
        indexMap.put("index5", Collections.emptySet());
        indexMap.put("index6", Collections.emptySet());

        when(indexSet.getAllIndexAliases()).thenReturn(indexMap);
        when(indexSet.getManagedIndices()).thenReturn(indexMap.keySet().stream().toArray(String[]::new));
        when(indexSet.extractIndexNumber(anyString())).then(this::extractIndexNumber);

        retentionStrategy = spy(new AbstractIndexCountBasedRetentionStrategy(indices, activityWriter) {
            @Override
            protected Optional<Integer> getMaxNumberOfIndices(IndexSet indexSet) {
                return null;
            }

            @Override
            protected void retain(String indexName, IndexSet indexSet) {

            }

            @Override
            public Class<? extends RetentionStrategyConfig> configurationClass() {
                return null;
            }

            @Override
            public RetentionStrategyConfig defaultConfiguration() {
                return null;
            }
        });

        when(retentionStrategy.getMaxNumberOfIndices(eq(indexSet))).thenReturn(Optional.of(5));
        when(indices.isReopened(anyString())).thenReturn(false);
    }

    @Test
    public void shouldRetainOldestIndex() throws Exception {
        retentionStrategy.retain(indexSet);

        final ArgumentCaptor<String> retainedIndexName = ArgumentCaptor.forClass(String.class);
        verify(retentionStrategy, times(1)).retain(retainedIndexName.capture(), eq(indexSet));
        assertThat(retainedIndexName.getValue()).isEqualTo("index1");

        verify(activityWriter, times(2)).write(any(Activity.class));
    }

    @Test
    public void shouldRetainOldestIndices() throws Exception {
        when(retentionStrategy.getMaxNumberOfIndices(eq(indexSet))).thenReturn(Optional.of(4));

        retentionStrategy.retain(indexSet);

        final ArgumentCaptor<String> retainedIndexName = ArgumentCaptor.forClass(String.class);
        verify(retentionStrategy, times(2)).retain(retainedIndexName.capture(), eq(indexSet));
        // Ensure that the oldest indices come first
        assertThat(retainedIndexName.getAllValues()).containsExactly("index1", "index2");

        verify(activityWriter, times(3)).write(any(Activity.class));
    }

    @Test
    public void shouldIgnoreReopenedIndexWhenCountingAgainstLimit() {
        when(indices.isReopened(eq("index1"))).thenReturn(true);

        retentionStrategy.retain(indexSet);

        verify(retentionStrategy, never()).retain(anyString(), eq(indexSet));

        verify(activityWriter, never()).write(any(Activity.class));
    }

    @Test
    public void shouldIgnoreReopenedIndexWhenDeterminingRetainedIndices() {
        when(retentionStrategy.getMaxNumberOfIndices(eq(indexSet))).thenReturn(Optional.of(4));
        when(indices.isReopened(eq("index1"))).thenReturn(true);

        retentionStrategy.retain(indexSet);

        final ArgumentCaptor<String> retainedIndexName = ArgumentCaptor.forClass(String.class);
        verify(retentionStrategy, times(1)).retain(retainedIndexName.capture(), eq(indexSet));
        assertThat(retainedIndexName.getValue()).isEqualTo("index2");

        verify(activityWriter, times(2)).write(any(Activity.class));
    }

    @Test
    public void shouldIgnoreWriteAliasWhenDeterminingRetainedIndices() {
        final String indexWithWriteIndexAlias = "index1";
        final String writeIndexAlias = "WriteIndexAlias";

        when(indexSet.getWriteIndexAlias()).thenReturn(writeIndexAlias);
        indexMap.put(indexWithWriteIndexAlias, Collections.singleton(writeIndexAlias));

        retentionStrategy.retain(indexSet);

        final ArgumentCaptor<String> retainedIndexName = ArgumentCaptor.forClass(String.class);
        verify(retentionStrategy, times(1)).retain(retainedIndexName.capture(), eq(indexSet));
        assertThat(retainedIndexName.getValue()).isEqualTo("index2");

        verify(activityWriter, times(2)).write(any(Activity.class));
    }

    private Optional<Integer> extractIndexNumber(InvocationOnMock invocation) {
        return Optional.of(Integer.parseInt(((String)invocation.getArgument(0)).replace("index", "")));
    }
}
