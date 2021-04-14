/*
 * */
package com.synectiks.process.server.security;

import javax.annotation.Nullable;

import com.synectiks.process.server.plugin.database.PersistedService;

import java.util.Collection;

public interface MongoDBSessionService extends PersistedService {
    @Nullable
    MongoDbSession load(String sessionId);

    Collection<MongoDbSession> loadAll();
}
