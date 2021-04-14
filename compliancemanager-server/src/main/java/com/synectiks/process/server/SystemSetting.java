/*
 * */
package com.synectiks.process.server;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.PersistedServiceImpl;

/**
 *  @author Lennart Koopmann <lennart@socketfeed.com>
 */
public class SystemSetting extends PersistedServiceImpl {
    
    private static final String COLLECTION_NAME = "system_settings";

    public SystemSetting(MongoConnection mongoConnection) {
        super(mongoConnection);
    }
    
    public boolean getBoolean(String key) {
        DBCollection coll = getCollection();
        
        DBObject query = new BasicDBObject();
        query.put("key", key);
        
        DBObject result = coll.findOne(query);
        if (result == null) {
            return false;
        }
        
        if (result.get("value").equals(true)) {
            return true;
        }
        
        return false;
    }
    
    public BasicDBList getList(String key) {
        DBCollection coll = getCollection();
        DBObject query = new BasicDBObject();
        query.put("key", key);

        DBObject result = coll.findOne(query);

        return (BasicDBList) result.get("value");
    }

    private DBCollection getCollection() {
        return mongoConnection.getDatabase().getCollection(COLLECTION_NAME);
    }
    
}
