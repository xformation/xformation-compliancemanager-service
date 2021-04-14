/*
 * */
package com.synectiks.process.server.database;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.client.MongoDatabase;

public interface MongoConnection {
    Mongo connect();

    /**
     * Get instance of the configured MongoDB database.
     *
     * @return The configured MongoDB database.
     * @deprecated Use {@link #getMongoDatabase()}.
     */
    @Deprecated
    DB getDatabase();

    /**
     * Get instance of the configured MongoDB database.
     *
     * @return The configured MongoDB database.
     */
    MongoDatabase getMongoDatabase();
}
