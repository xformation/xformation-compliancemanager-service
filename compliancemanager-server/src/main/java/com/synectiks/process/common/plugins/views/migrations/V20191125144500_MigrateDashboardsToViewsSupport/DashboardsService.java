/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport;

import com.google.common.collect.Streams;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;

import org.bson.types.ObjectId;
import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;

import javax.inject.Inject;
import java.util.stream.Stream;

class DashboardsService {
    private static final String COLLECTION_NAME = "dashboards";
    private final JacksonDBCollection<Dashboard, ObjectId> db;

    @Inject
    DashboardsService(MongoConnection mongoConnection, MongoJackObjectMapperProvider mapper) {
        this.db = JacksonDBCollection.wrap(mongoConnection.getDatabase().getCollection(COLLECTION_NAME),
                Dashboard.class,
                ObjectId.class,
                mapper.get());
    }

    Stream<Dashboard> streamAll() {
        final DBCursor<Dashboard> cursor = db.find(DBQuery.empty());
        return Streams.stream((Iterable<Dashboard>) cursor).onClose(cursor::close);
    }
}
