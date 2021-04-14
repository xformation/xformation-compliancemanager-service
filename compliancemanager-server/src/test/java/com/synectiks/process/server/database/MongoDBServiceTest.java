/*
 * */
package com.synectiks.process.server.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synectiks.process.common.testing.mongodb.MongoDBInstance;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.junit.Rule;


public class MongoDBServiceTest {
    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();

    protected final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    protected final MongoJackObjectMapperProvider mapperProvider = new MongoJackObjectMapperProvider(objectMapper);
}
