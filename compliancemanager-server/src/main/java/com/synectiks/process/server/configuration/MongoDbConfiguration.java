/*
 * */
package com.synectiks.process.server.configuration;

import com.github.joschi.jadconfig.Parameter;
import com.github.joschi.jadconfig.ValidationException;
import com.github.joschi.jadconfig.ValidatorMethod;
import com.github.joschi.jadconfig.validators.PositiveIntegerValidator;
import com.github.joschi.jadconfig.validators.StringNotBlankValidator;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;

public class MongoDbConfiguration {
    @Parameter(value = "mongodb_max_connections", validator = PositiveIntegerValidator.class)
    private int maxConnections = 1000;

    @Parameter(value = "mongodb_threads_allowed_to_block_multiplier", validator = PositiveIntegerValidator.class)
    private int threadsAllowedToBlockMultiplier = 5;

    @Parameter(value = "mongodb_uri", required = true, validator = StringNotBlankValidator.class)
    private String uri = "mongodb://localhost/compliancemanager";


    public int getMaxConnections() {
        return maxConnections;
    }

    public int getThreadsAllowedToBlockMultiplier() {
        return threadsAllowedToBlockMultiplier;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public MongoClientURI getMongoClientURI() {
        final MongoClientOptions.Builder mongoClientOptionsBuilder = MongoClientOptions.builder()
                .connectionsPerHost(getMaxConnections())
                .threadsAllowedToBlockForConnectionMultiplier(getThreadsAllowedToBlockMultiplier());

        return new MongoClientURI(uri, mongoClientOptionsBuilder);
    }

    @ValidatorMethod
    public void validate() throws ValidationException {
        if(getMongoClientURI() == null) {
            throw new ValidationException("mongodb_uri is not a valid MongoDB connection string");
        }
    }
}
