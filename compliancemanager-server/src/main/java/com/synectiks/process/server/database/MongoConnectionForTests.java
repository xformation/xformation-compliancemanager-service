/*
 * */
package com.synectiks.process.server.database;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import static java.util.Objects.requireNonNull;

public class MongoConnectionForTests implements MongoConnection {
    private final Mongo mongoClient;
    private final DB db;
    private final MongoDatabase mongoDatabase;

    public MongoConnectionForTests(Mongo mongoClient, String dbName) {
        this.mongoClient = requireNonNull(mongoClient);
        this.db = mongoClient.getDB(dbName);
        this.mongoDatabase = null;
    }

    public MongoConnectionForTests(MongoClient mongoClient, String dbName) {
        this.mongoClient = requireNonNull(mongoClient);
        this.db = mongoClient.getDB(dbName);
        this.mongoDatabase = mongoClient.getDatabase(dbName);
    }

    @Override
    public Mongo connect() {
        return mongoClient;
    }

    @Override
    public DB getDatabase() {
        return db;
    }

    @Override
    public MongoDatabase getMongoDatabase() {
        if(mongoDatabase == null) {
            throw new IllegalStateException("MongoDatabase is unavailable.");
        }

        return mongoDatabase;
    }
}
