/*
 * */
package com.synectiks.process.server.migrations;

import com.mongodb.client.MongoCollection;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBInstance;
import com.synectiks.process.server.contentpacks.ContentPackPersistenceService;
import com.synectiks.process.server.contentpacks.model.ContentPack;
import com.synectiks.process.server.migrations.V20180718155800_AddContentPackIdAndRev;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.ZonedDateTime;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.exists;
import static org.assertj.core.api.Assertions.assertThat;

public class V20180718155800_AddContentPackIdAndRevTest {
    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();

    private V20180718155800_AddContentPackIdAndRev migration;

    @Before
    public void setUp() {
        this.migration = new V20180718155800_AddContentPackIdAndRev(mongodb.mongoConnection());
    }

    @Test
    public void createdAt() {
        assertThat(migration.createdAt()).isEqualTo(ZonedDateTime.parse("2018-07-18T15:58:00Z"));
    }

    @Test
    @MongoDBFixtures("V20180718155800_AddContentPackIdAndRevTest.json")
    public void upgrade() {
        final MongoCollection<Document> collection = mongodb.mongoConnection()
                .getMongoDatabase()
                .getCollection(ContentPackPersistenceService.COLLECTION_NAME);
        final Bson filter = and(exists(ContentPack.FIELD_META_ID), exists(ContentPack.FIELD_META_REVISION));

        assertThat(collection.count(filter)).isEqualTo(1L);
        migration.upgrade();
        assertThat(collection.count(filter)).isEqualTo(2L);
    }
}
