/*
 * */
package com.synectiks.process.server.security;

import javax.annotation.Nullable;

import com.synectiks.process.server.plugin.database.PersistedService;
import com.synectiks.process.server.plugin.database.ValidationException;

import java.util.List;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public interface AccessTokenService extends PersistedService {
    @SuppressWarnings("unchecked")
    AccessToken load(String token);

    @Nullable
    AccessToken loadById(String id);

    @SuppressWarnings("unchecked")
    List<AccessToken> loadAll(String username);

    AccessToken create(String username, String name);

    void touch(AccessToken accessToken) throws ValidationException;

    String save(AccessToken accessToken) throws ValidationException;

    int deleteAllForUser(String username);
}
