/*
 * */
package com.synectiks.process.server.bindings.providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.configuration.MongoDbConfiguration;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.MongoConnectionImpl;

import javax.inject.Inject;
import javax.inject.Provider;

public class MongoConnectionProvider implements Provider<MongoConnection> {
    private static final Logger LOG = LoggerFactory.getLogger(MongoConnectionProvider.class);
    private static MongoConnection mongoConnection = null;

    @Inject
    public MongoConnectionProvider(MongoDbConfiguration configuration) {
        if (mongoConnection == null) {
            try {
                mongoConnection = new MongoConnectionImpl(configuration);

                mongoConnection.connect();
            } catch (Exception e) {
                LOG.error("Error connecting to MongoDB: {}", e.getMessage());
                throw e;
            }
        }
    }

    @Override
    public MongoConnection get() {
        return mongoConnection;
    }
}
