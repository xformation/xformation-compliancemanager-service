/*
 * */
package com.synectiks.process.server.migrations;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.indexer.indexset.IndexSetService;
import com.synectiks.process.server.indexer.management.IndexManagementConfig;
import com.synectiks.process.server.indexer.retention.strategies.DeletionRetentionStrategy;
import com.synectiks.process.server.indexer.retention.strategies.DeletionRetentionStrategyConfig;
import com.synectiks.process.server.indexer.rotation.strategies.MessageCountRotationStrategy;
import com.synectiks.process.server.indexer.rotation.strategies.MessageCountRotationStrategyConfig;
import com.synectiks.process.server.migrations.Migration;
import com.synectiks.process.server.migrations.V20161124104700_AddRetentionRotationAndDefaultFlagToIndexSetMigration;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.indexer.retention.RetentionStrategyConfig;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategyConfig;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class V20161124104700_AddRetentionRotationAndDefaultFlagToIndexSetMigrationTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private IndexSetService indexSetService;
    @Mock
    private ClusterConfigService clusterConfigService;

    private Migration migration;

    @Before
    public void setUp() throws Exception {
        migration = new V20161124104700_AddRetentionRotationAndDefaultFlagToIndexSetMigration(indexSetService, clusterConfigService);
    }

    @Test
    public void createdAt() throws Exception {
        // Test the date to detect accidental changes to it.
        assertThat(migration.createdAt()).isEqualTo(ZonedDateTime.parse("2016-11-24T10:47:00Z"));
    }

    @Test
    public void upgrade() throws Exception {
        final String rotationStrategyClass = MessageCountRotationStrategy.class.getCanonicalName();
        final String retentionStrategyClass = DeletionRetentionStrategy.class.getCanonicalName();
        final RotationStrategyConfig rotationStrategy = MessageCountRotationStrategyConfig.createDefault();
        final RetentionStrategyConfig retentionStrategy = DeletionRetentionStrategyConfig.createDefault();

        final IndexSetConfig config1 = IndexSetConfig.builder()
                .id("id-1")
                .title("title-1")
                .indexPrefix("prefix-1")
                .shards(1)
                .replicas(0)
                .rotationStrategy(rotationStrategy)
                .retentionStrategy(retentionStrategy)
                .creationDate(ZonedDateTime.of(2016, 10, 12, 0, 0, 0, 0, ZoneOffset.UTC))
                .indexAnalyzer("standard")
                .indexTemplateName("template-1")
                .indexOptimizationMaxNumSegments(1)
                .indexOptimizationDisabled(false)
                .build();
        final IndexSetConfig config2 = IndexSetConfig.builder()
                .id("id-2")
                .title("title-2")
                .indexPrefix("prefix-2")
                .shards(1)
                .replicas(0)
                .rotationStrategy(rotationStrategy)
                .retentionStrategy(retentionStrategy)
                .creationDate(ZonedDateTime.of(2016, 10, 10, 0, 0, 0, 0, ZoneOffset.UTC))
                .indexAnalyzer("standard")
                .indexTemplateName("template-2")
                .indexOptimizationMaxNumSegments(1)
                .indexOptimizationDisabled(false)
                .build();

        when(clusterConfigService.get(IndexManagementConfig.class)).thenReturn(IndexManagementConfig.create(rotationStrategyClass , retentionStrategyClass));
        when(indexSetService.findAll()).thenReturn(Lists.newArrayList(config1, config2));

        migration.upgrade();

        verify(indexSetService).save(config1.toBuilder()
                .rotationStrategyClass(rotationStrategyClass)
                .retentionStrategyClass(retentionStrategyClass)
                .build());
        verify(indexSetService).save(config2.toBuilder()
                .rotationStrategyClass(rotationStrategyClass)
                .retentionStrategyClass(retentionStrategyClass)
                .build());

        verify(clusterConfigService).write(V20161124104700_AddRetentionRotationAndDefaultFlagToIndexSetMigration.MigrationCompleted.create(
                ImmutableSet.of("id-1", "id-2"), Collections.emptySet(), "id-2"
        ));
    }

    @Test
    public void upgradeWhenOneAlreadyHasStrategiesSet() throws Exception {
        final String rotationStrategyClass = MessageCountRotationStrategy.class.getCanonicalName();
        final String retentionStrategyClass = DeletionRetentionStrategy.class.getCanonicalName();
        final RotationStrategyConfig rotationStrategy = MessageCountRotationStrategyConfig.createDefault();
        final RetentionStrategyConfig retentionStrategy = DeletionRetentionStrategyConfig.createDefault();

        final IndexSetConfig config1 = IndexSetConfig.builder()
                .id("id-1")
                .title("title-1")
                .indexPrefix("prefix-1")
                .shards(1)
                .replicas(0)
                // Does not have the rotation strategy class name!
                .rotationStrategy(rotationStrategy)
                .retentionStrategyClass(retentionStrategyClass)
                .retentionStrategy(retentionStrategy)
                .creationDate(ZonedDateTime.of(2016, 10, 12, 0, 0, 0, 0, ZoneOffset.UTC))
                .indexAnalyzer("standard")
                .indexTemplateName("template-1")
                .indexOptimizationMaxNumSegments(1)
                .indexOptimizationDisabled(false)
                .build();
        final IndexSetConfig config2 = IndexSetConfig.builder()
                .id("id-2")
                .title("title-2")
                .indexPrefix("prefix-2")
                .shards(1)
                .replicas(0)
                .rotationStrategyClass(rotationStrategyClass)
                .rotationStrategy(rotationStrategy)
                .retentionStrategyClass(retentionStrategyClass)
                .retentionStrategy(retentionStrategy)
                .creationDate(ZonedDateTime.of(2016, 10, 13, 0, 0, 0, 0, ZoneOffset.UTC))
                .indexAnalyzer("standard")
                .indexTemplateName("template-2")
                .indexOptimizationMaxNumSegments(1)
                .indexOptimizationDisabled(false)
                .build();

        when(clusterConfigService.get(IndexManagementConfig.class)).thenReturn(IndexManagementConfig.create(rotationStrategyClass, retentionStrategyClass));
        when(indexSetService.findAll()).thenReturn(Lists.newArrayList(config1, config2));

        migration.upgrade();

        verify(indexSetService).save(config1.toBuilder().rotationStrategyClass(rotationStrategyClass).build());
        verify(indexSetService, never()).save(config2);

        verify(clusterConfigService).write(V20161124104700_AddRetentionRotationAndDefaultFlagToIndexSetMigration.MigrationCompleted.create(
                Collections.singleton("id-1"), Collections.singleton("id-2"), "id-1"
        ));
    }

    @Test
    public void upgradeWithoutIndexManagementConfig() throws Exception {
        when(clusterConfigService.get(IndexManagementConfig.class)).thenReturn(null);

        expectedException.expect(IllegalStateException.class);

        migration.upgrade();

        verify(indexSetService, never()).save(any(IndexSetConfig.class));
        verify(clusterConfigService, never()).write(V20161124104700_AddRetentionRotationAndDefaultFlagToIndexSetMigration.MigrationCompleted.class);
    }

    @Test
    public void upgradeWithWrongRotationPrefix() throws Exception {
        final String rotationStrategyClass = "foo";
        final String retentionStrategyClass = DeletionRetentionStrategy.class.getCanonicalName();

        final RotationStrategyConfig rotationStrategy = MessageCountRotationStrategyConfig.createDefault();
        final RetentionStrategyConfig retentionStrategy = DeletionRetentionStrategyConfig.createDefault();

        final IndexSetConfig config1 = IndexSetConfig.builder()
                .id("id-1")
                .title("title-1")
                .indexPrefix("prefix-1")
                .shards(1)
                .replicas(0)
                .rotationStrategy(rotationStrategy)
                .retentionStrategy(retentionStrategy)
                .creationDate(ZonedDateTime.of(2016, 10, 12, 0, 0, 0, 0, ZoneOffset.UTC))
                .indexAnalyzer("standard")
                .indexTemplateName("template-1")
                .indexOptimizationMaxNumSegments(1)
                .indexOptimizationDisabled(false)
                .build();
        final IndexSetConfig config2 = IndexSetConfig.builder()
                .id("id-2")
                .title("title-2")
                .indexPrefix("prefix-2")
                .shards(1)
                .replicas(0)
                .rotationStrategy(rotationStrategy)
                .retentionStrategy(retentionStrategy)
                .creationDate(ZonedDateTime.of(2016, 10, 13, 0, 0, 0, 0, ZoneOffset.UTC))
                .indexAnalyzer("standard")
                .indexTemplateName("template-2")
                .indexOptimizationMaxNumSegments(1)
                .indexOptimizationDisabled(false)
                .build();

        when(clusterConfigService.get(IndexManagementConfig.class)).thenReturn(IndexManagementConfig.create(rotationStrategyClass , retentionStrategyClass));
        when(indexSetService.findAll()).thenReturn(Lists.newArrayList(config1, config2));

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("rotation strategy config type <");

        migration.upgrade();

        verify(indexSetService, never()).save(any(IndexSetConfig.class));
        verify(clusterConfigService, never()).write(V20161124104700_AddRetentionRotationAndDefaultFlagToIndexSetMigration.MigrationCompleted.class);
    }

    @Test
    public void upgradeWithWrongRetentionPrefix() throws Exception {
        final String rotationStrategyClass = MessageCountRotationStrategy.class.getCanonicalName();
        final String retentionStrategyClass = "bar";

        final RotationStrategyConfig rotationStrategy = MessageCountRotationStrategyConfig.createDefault();
        final RetentionStrategyConfig retentionStrategy = DeletionRetentionStrategyConfig.createDefault();

        final IndexSetConfig config1 = IndexSetConfig.builder()
                .id("id-1")
                .title("title-1")
                .indexPrefix("prefix-1")
                .shards(1)
                .replicas(0)
                .rotationStrategy(rotationStrategy)
                .retentionStrategy(retentionStrategy)
                .creationDate(ZonedDateTime.of(2016, 10, 12, 0, 0, 0, 0, ZoneOffset.UTC))
                .indexAnalyzer("standard")
                .indexTemplateName("template-1")
                .indexOptimizationMaxNumSegments(1)
                .indexOptimizationDisabled(false)
                .build();
        final IndexSetConfig config2 = IndexSetConfig.builder()
                .id("id-2")
                .title("title-2")
                .indexPrefix("prefix-2")
                .shards(1)
                .replicas(0)
                .rotationStrategy(rotationStrategy)
                .retentionStrategy(retentionStrategy)
                .creationDate(ZonedDateTime.of(2016, 10, 13, 0, 0, 0, 0, ZoneOffset.UTC))
                .indexAnalyzer("standard")
                .indexTemplateName("template-2")
                .indexOptimizationMaxNumSegments(1)
                .indexOptimizationDisabled(false)
                .build();

        when(clusterConfigService.get(IndexManagementConfig.class)).thenReturn(IndexManagementConfig.create(rotationStrategyClass , retentionStrategyClass));
        when(indexSetService.findAll()).thenReturn(Lists.newArrayList(config1, config2));

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("retention strategy config type <");

        migration.upgrade();

        verify(indexSetService, never()).save(any(IndexSetConfig.class));
        verify(clusterConfigService, never()).write(V20161124104700_AddRetentionRotationAndDefaultFlagToIndexSetMigration.MigrationCompleted.class);
    }

    @Test
    public void upgradeDoeNotRunWhenAlreadyComplete() throws Exception {
        final String rotationStrategyClass = MessageCountRotationStrategy.class.getCanonicalName();
        final String retentionStrategyClass = DeletionRetentionStrategy.class.getCanonicalName();
        final RotationStrategyConfig rotationStrategy = MessageCountRotationStrategyConfig.createDefault();
        final RetentionStrategyConfig retentionStrategy = DeletionRetentionStrategyConfig.createDefault();

        final IndexSetConfig config1 = IndexSetConfig.builder()
                .id("id-1")
                .title("title-1")
                .indexPrefix("prefix-1")
                .shards(1)
                .replicas(0)
                .rotationStrategy(rotationStrategy)
                .retentionStrategy(retentionStrategy)
                .creationDate(ZonedDateTime.of(2016, 10, 12, 0, 0, 0, 0, ZoneOffset.UTC))
                .indexAnalyzer("standard")
                .indexTemplateName("template-1")
                .indexOptimizationMaxNumSegments(1)
                .indexOptimizationDisabled(false)
                .build();
        final IndexSetConfig config2 = IndexSetConfig.builder()
                .id("id-2")
                .title("title-2")
                .indexPrefix("prefix-2")
                .shards(1)
                .replicas(0)
                .rotationStrategy(rotationStrategy)
                .retentionStrategy(retentionStrategy)
                .creationDate(ZonedDateTime.of(2016, 10, 13, 0, 0, 0, 0, ZoneOffset.UTC))
                .indexAnalyzer("standard")
                .indexTemplateName("template-2")
                .indexOptimizationMaxNumSegments(1)
                .indexOptimizationDisabled(false)
                .build();

        when(clusterConfigService.get(IndexManagementConfig.class)).thenReturn(IndexManagementConfig.create(rotationStrategyClass , retentionStrategyClass));
        when(indexSetService.findAll()).thenReturn(Lists.newArrayList(config1, config2));

        when(clusterConfigService.get(V20161124104700_AddRetentionRotationAndDefaultFlagToIndexSetMigration.MigrationCompleted.class))
                .thenReturn(V20161124104700_AddRetentionRotationAndDefaultFlagToIndexSetMigration.MigrationCompleted.create(Collections.emptySet(), Collections.emptySet(), "id-1"));

        migration.upgrade();

        verify(indexSetService, never()).save(any(IndexSetConfig.class));
        verify(clusterConfigService, never()).write(V20161124104700_AddRetentionRotationAndDefaultFlagToIndexSetMigration.MigrationCompleted.class);
    }
}