/*
 * */
package com.synectiks.process.common.plugins.views.migrations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.synectiks.process.common.plugins.views.migrations.V20200409083200_RemoveRootQueriesFromMigratedDashboards;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBInstance;
import com.synectiks.process.server.migrations.Migration;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;
import static com.synectiks.process.common.plugins.views.migrations.V20200409083200_RemoveRootQueriesFromMigratedDashboards.MigrationCompleted;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class V20200409083200_RemoveRootQueriesFromMigratedDashboardsTest {
    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ClusterConfigService clusterConfigService;

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    private Migration migration;

    private MongoCollection<Document> viewsCollection;
    private MongoCollection<Document> searchesCollection;

    @Before
    public void setUp() {
        this.searchesCollection = spy(mongodb.mongoConnection().getMongoDatabase().getCollection("searches"));
        this.viewsCollection = spy(mongodb.mongoConnection().getMongoDatabase().getCollection("views"));
        this.migration = new V20200409083200_RemoveRootQueriesFromMigratedDashboards(clusterConfigService, this.viewsCollection, this.searchesCollection);
    }

    @Test
    public void runsIfNoDashboardsArePresent() {
        this.migration.upgrade();
    }

    @Test
    public void writesMigrationCompletedAfterSuccess() {
        this.migration.upgrade();

        final MigrationCompleted migrationCompleted = captureMigrationCompleted();
        assertThat(migrationCompleted.modifiedViewsCount()).isZero();
    }

    @Test
    public void doesNotRunIfMigrationHasCompletedBefore() {
        when(clusterConfigService.get(MigrationCompleted.class)).thenReturn(MigrationCompleted.create(0));

        this.migration.upgrade();

        verify(viewsCollection, never()).find(any(Bson.class));
        verify(searchesCollection, never()).find(any(Bson.class));
    }

    @Test
    @MongoDBFixtures("V20200409083200_RemoveRootQueriesFromMigratedDashboards/sample.json")
    public void findsCorrectDocuments() throws JsonProcessingException, JSONException {
        migration.upgrade();

        assertThat(rootQueryStrings("5d6ce7bd5d1eb45af534399e")).allMatch(String::isEmpty);
        assertThat(rootQueryStrings("5da9bc1b3a6a1d0d2f07faf2")).allMatch(String::isEmpty);
        assertThat(rootQueryStrings("5dad673d6131be4f08ceea77")).allMatch(String::isEmpty);
        assertThat(rootQueryStrings("5dbbf604799412036075d78f")).allMatch(String::isEmpty);

        assertThat(rootQueryStrings("5da9bbb944300ca38bc5da3e")).containsExactlyInAnyOrder("author:\"$author$\" AND project:\"compliancemanager2-server\"", "author:\"$author$\"");
        assertThat(rootQueryStrings("5da9bbba12993f3904b41217")).containsExactlyInAnyOrder("author:\"$author$\" AND project:\"compliancemanager2-server\"", "author:\"$author$\"");
    }

    private MongoCollection<Document> afterSearchesCollection() {
        return mongodb.mongoConnection()
                .getMongoDatabase()
                .getCollection("searches");
    }
    private List<String> rootQueryStrings(String searchId) {
        //noinspection unchecked
        return StreamSupport.stream(afterSearchesCollection().find(eq("_id", new ObjectId(searchId))).spliterator(), false)
                .flatMap(root -> (Stream<Document>)root.get("queries", List.class).stream())
                .map(query -> query.get("query", Document.class).getString("query_string"))
                .collect(Collectors.toList());
    }

    private MigrationCompleted captureMigrationCompleted() {
        final ArgumentCaptor<MigrationCompleted> migrationCompletedCaptor = ArgumentCaptor.forClass(MigrationCompleted.class);
        verify(clusterConfigService, times(1)).write(migrationCompletedCaptor.capture());
        return migrationCompletedCaptor.getValue();
    }
}
