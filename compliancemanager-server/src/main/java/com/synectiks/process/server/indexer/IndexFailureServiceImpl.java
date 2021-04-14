/*
 * */
package com.synectiks.process.server.indexer;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.synectiks.process.server.database.CollectionName;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.PersistedServiceImpl;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class IndexFailureServiceImpl extends PersistedServiceImpl implements IndexFailureService {
    @Inject
    public IndexFailureServiceImpl(MongoConnection mongoConnection) {
        super(mongoConnection);

        // Make sure that the index failures collection is always created capped.
        final String collectionName = IndexFailureImpl.class.getAnnotation(CollectionName.class).value();
        if (!mongoConnection.getDatabase().collectionExists(collectionName)) {
            final DBObject options = BasicDBObjectBuilder.start()
                    .add("capped", true)
                    .add("size", 52428800) // 50MB max size.
                    .get();

            final DBCollection collection = mongoConnection.getDatabase().createCollection(collectionName, options);

            collection.createIndex(new BasicDBObject("timestamp", 1));
            collection.createIndex(new BasicDBObject("letter_id", 1));
        }
    }

    @Override
    public List<IndexFailure> all(int limit, int offset) {
        final DBObject sort = new BasicDBObject("$natural", -1);
        final List<DBObject> results = query(IndexFailureImpl.class, new BasicDBObject(), sort, limit, offset);
        final List<IndexFailure> failures = new ArrayList<>(results.size());
        for (DBObject o : results) {
            failures.add(new IndexFailureImpl((ObjectId) o.get("_id"), o.toMap()));
        }

        return failures;
    }

    @Override
    public long countSince(DateTime since) {
        final BasicDBObject query = new BasicDBObject("timestamp", new BasicDBObject("$gte", since.toDate()));

        return count(IndexFailureImpl.class, query);
    }

    @Override
    public long totalCount() {
        return collection(IndexFailureImpl.class).count();
    }
}