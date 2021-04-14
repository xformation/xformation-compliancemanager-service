/*
 * */
package com.synectiks.process.server.migrations;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.indexer.IndexSet;
import com.synectiks.process.server.indexer.IndexSetRegistry;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.migrations.Migration;
import com.synectiks.process.server.migrations.V20161116172200_CreateDefaultStreamMigration;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.streams.StreamImpl;
import com.synectiks.process.server.streams.StreamService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class V20161116172200_CreateDefaultStreamMigrationTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private Migration migration;
    @Mock
    private StreamService streamService;
    @Mock
    private IndexSetRegistry indexSetRegistry;
    @Mock
    private IndexSet indexSet;
    @Mock
    private IndexSetConfig indexSetConfig;

    @Before
    public void setUpService() {
        migration = new V20161116172200_CreateDefaultStreamMigration(streamService, indexSetRegistry);

        when(indexSet.getConfig()).thenReturn(indexSetConfig);
        when(indexSetConfig.id()).thenReturn("abc123");
    }

    @Test
    public void upgrade() throws Exception {
        final ArgumentCaptor<Stream> streamArgumentCaptor = ArgumentCaptor.forClass(Stream.class);
        when(streamService.load("000000000000000000000001")).thenThrow(NotFoundException.class);
        when(indexSetRegistry.getDefault()).thenReturn(indexSet);

        migration.upgrade();

        verify(streamService).save(streamArgumentCaptor.capture());

        final Stream stream = streamArgumentCaptor.getValue();
        assertThat(stream.getTitle()).isEqualTo("All messages");
        assertThat(stream.getDisabled()).isFalse();
        assertThat(stream.getMatchingType()).isEqualTo(StreamImpl.MatchingType.DEFAULT);
    }

    @Test
    public void upgradeWithoutDefaultIndexSet() throws Exception {
        when(streamService.load("000000000000000000000001")).thenThrow(NotFoundException.class);
        when(indexSetRegistry.getDefault()).thenThrow(IllegalStateException.class);

        expectedException.expect(IllegalStateException.class);

        migration.upgrade();
    }

    @Test
    public void upgradeDoesNotRunIfDefaultStreamExists() throws Exception {
        when(streamService.load("000000000000000000000001")).thenReturn(mock(Stream.class));

        migration.upgrade();

        verify(streamService, never()).save(any(Stream.class));
    }
}