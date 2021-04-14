/*
 * */
package com.synectiks.process.server.system.activities;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.PersistedServiceImpl;

import org.bson.types.ObjectId;
import org.mongojack.DBSort;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class SystemMessageServiceImpl extends PersistedServiceImpl implements SystemMessageService {
    private final int PER_PAGE = 30;

    @Inject
    public SystemMessageServiceImpl(MongoConnection mongoConnection) {
        super(mongoConnection);
        final DBCollection collection = this.collection(SystemMessageImpl.class);
        collection.createIndex(DBSort.desc("timestamp"));
    }

    @Override
    public List<SystemMessage> all(int page) {
        List<SystemMessage> messages = Lists.newArrayList();

        DBObject sort = new BasicDBObject();
        sort.put("timestamp", -1);

        List<DBObject> results = query(SystemMessageImpl.class, new BasicDBObject(), sort, PER_PAGE, PER_PAGE * page);
        for (DBObject o : results) {
            messages.add(new SystemMessageImpl(new ObjectId(o.get("_id").toString()), o.toMap()));
        }

        return messages;
    }

    @Override
    public long totalCount() {
        return super.totalCount(SystemMessageImpl.class);
    }

    @Override
    public SystemMessage create(Map<String, Object> fields) {
        return new SystemMessageImpl(fields);
    }
}
