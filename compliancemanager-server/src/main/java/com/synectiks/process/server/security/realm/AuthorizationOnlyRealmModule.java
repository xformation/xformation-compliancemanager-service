/*
 * */
package com.synectiks.process.server.security.realm;

import com.google.inject.Scopes;
import com.google.inject.multibindings.MapBinder;
import com.synectiks.process.server.plugin.inject.Graylog2Module;

import org.apache.shiro.realm.AuthorizingRealm;

public class AuthorizationOnlyRealmModule extends Graylog2Module {

    @Override
    protected void configure() {
        final MapBinder<String, AuthorizingRealm> authz = authorizationOnlyRealmBinder();

        add(authz, MongoDbAuthorizationRealm.NAME, MongoDbAuthorizationRealm.class);
    }

    private void add(MapBinder<String, AuthorizingRealm> authz, String name,
                     Class<? extends AuthorizingRealm> realm) {
        authz.addBinding(name).to(realm).in(Scopes.SINGLETON);
    }
}
