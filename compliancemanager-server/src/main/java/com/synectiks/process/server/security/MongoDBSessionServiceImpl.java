/*
 * */
package com.synectiks.process.server.security;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.PersistedServiceImpl;

import org.bson.Document;
import org.bson.types.ObjectId;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;

@Singleton
public class MongoDBSessionServiceImpl extends PersistedServiceImpl implements MongoDBSessionService {
    @Inject
    public MongoDBSessionServiceImpl(MongoConnection mongoConnection) {
        super(mongoConnection);

        final MongoDatabase database = mongoConnection.getMongoDatabase();
        final MongoCollection<Document> sessions = database.getCollection(MongoDbSession.COLLECTION_NAME);
        sessions.createIndex(Indexes.ascending(MongoDbSession.FIELD_SESSION_ID));
    }

    @Override
    @Nullable
    public MongoDbSession load(String sessionId) {
        DBObject query = new BasicDBObject();
        query.put(MongoDbSession.FIELD_SESSION_ID, sessionId);

        DBObject result = findOne(MongoDbSession.class, query);
        if (result == null) {
            return null;
        }
        final Object objectId = result.get("_id");
        return new MongoDbSession((ObjectId) objectId, result.toMap());
    }

    @Override
    public Collection<MongoDbSession> loadAll() {
        DBObject query = new BasicDBObject();
        List<MongoDbSession> dbSessions = Lists.newArrayList();
        final List<DBObject> sessions = query(MongoDbSession.class, query);
        for (DBObject session : sessions) {
            dbSessions.add(new MongoDbSession((ObjectId) session.get("_id"), session.toMap()));
        }

        return dbSessions;
    }
}