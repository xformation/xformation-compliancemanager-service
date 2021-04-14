/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.savedsearch;

import com.google.common.collect.Streams;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;

import org.bson.types.ObjectId;
import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;

import javax.inject.Inject;
import java.util.stream.Stream;

public class SavedSearchService {
    private static final String COLLECTION_NAME = "saved_searches";
    private final JacksonDBCollection<SavedSearch, ObjectId> db;

    @Inject
    public SavedSearchService(MongoConnection mongoConnection, MongoJackObjectMapperProvider mapper) {
        this.db = JacksonDBCollection.wrap(mongoConnection.getDatabase().getCollection(COLLECTION_NAME),
                SavedSearch.class,
                ObjectId.class,
                mapper.get());
    }

    public Stream<SavedSearch> streamAll() {
        final DBCursor<SavedSearch> cursor = db.find(DBQuery.empty());
        return Streams.stream(cursor.iterator()).onClose(cursor::close);
    }
}

