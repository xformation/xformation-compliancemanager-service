/*
 * */
package com.synectiks.process.server.security;

import org.joda.time.DateTime;

import com.synectiks.process.server.plugin.database.Persisted;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public interface AccessToken extends Persisted {
    DateTime getLastAccess();

    String getUserName();

    void setUserName(String userName);

    String getToken();

    void setToken(String token);

    String getName();

    void setName(String name);
}
