/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport;

import org.bson.types.ObjectId;
import org.mongojack.JacksonDBCollection;

import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;

import javax.inject.Inject;

class SearchService {
    protected final JacksonDBCollection<Search, ObjectId> db;

    @Inject
    SearchService(MongoConnection mongoConnection, MongoJackObjectMapperProvider mapper) {
        db = JacksonDBCollection.wrap(mongoConnection.getDatabase().getCollection("searches"),
                Search.class,
                ObjectId.class,
                mapper.get());
    }

    public ObjectId save(Search search) {
        return db.insert(search).getSavedId();
    }

    public void remove(ObjectId searchID) { db.removeById(searchID); }

    long count() { return db.count(); }
}
