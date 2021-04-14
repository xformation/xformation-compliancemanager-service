/*
 * */
package com.synectiks.process.common.plugins.views.migrations;

import com.mongodb.client.MongoCollection;
import com.synectiks.process.common.plugins.views.migrations.V20190805115800_RemoveDashboardStateFromViews;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBInstance;
import com.synectiks.process.server.migrations.Migration;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;

import org.bson.Document;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class V20190805115800_RemoveDashboardStateFromViewsTest {
    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private ClusterConfigService clusterConfigService;

    @Test
    @MongoDBFixtures("V20190805115800_RemoveDashboardStateFromViewsTest.json")
    public void removesDashboardStateFromExistingViews() {
        final Migration migration = new V20190805115800_RemoveDashboardStateFromViews(clusterConfigService, mongodb.mongoConnection());

        migration.upgrade();

        final ArgumentCaptor<V20190805115800_RemoveDashboardStateFromViews.MigrationCompleted> argumentCaptor = ArgumentCaptor.forClass(V20190805115800_RemoveDashboardStateFromViews.MigrationCompleted.class);
        verify(clusterConfigService, times(1)).write(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().modifiedViewsCount()).isEqualTo(4);

        MongoCollection<Document> collection = mongodb.mongoConnection().getMongoDatabase().getCollection("views");
        assertThat(collection.count()).isEqualTo(4);
    }
}
