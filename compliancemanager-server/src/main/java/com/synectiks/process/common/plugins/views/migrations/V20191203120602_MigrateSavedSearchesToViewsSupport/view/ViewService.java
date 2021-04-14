/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view;

import org.bson.types.ObjectId;
import org.mongojack.JacksonDBCollection;

import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;

import javax.inject.Inject;

public class ViewService {
    protected final JacksonDBCollection<View, ObjectId> db;

    @Inject
    ViewService(MongoConnection mongoConnection, MongoJackObjectMapperProvider mapper) {
        this.db = JacksonDBCollection.wrap(mongoConnection.getDatabase().getCollection("views"),
                View.class,
                ObjectId.class,
                mapper.get());
    }

    public ObjectId save(View view) {
        return db.insert(view).getSavedId();
    }
}
