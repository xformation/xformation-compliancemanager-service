/*
 * */
package com.synectiks.process.server.security.realm;

import com.google.inject.Scopes;
import com.google.inject.multibindings.MapBinder;
import com.synectiks.process.server.Configuration;
import com.synectiks.process.server.plugin.inject.Graylog2Module;
import com.synectiks.process.server.security.OrderedAuthenticatingRealms;
import com.synectiks.process.server.security.StaticOrderedAuthenticatingRealms;

import org.apache.shiro.realm.AuthenticatingRealm;

import java.util.Set;

public class AuthenticatingRealmModule extends Graylog2Module {

    private final Set<String> deactivatedRealms;

    public AuthenticatingRealmModule(Configuration configuration) {
        this.deactivatedRealms = configuration.getDeactivatedBuiltinAuthenticationProviders();
    }

    @Override
    protected void configure() {
        final MapBinder<String, AuthenticatingRealm> auth = authenticationRealmBinder();

        bind(OrderedAuthenticatingRealms.class).to(StaticOrderedAuthenticatingRealms.class).in(Scopes.SINGLETON);

        add(auth, AccessTokenAuthenticator.NAME, AccessTokenAuthenticator.class);
        add(auth, RootAccountRealm.NAME, RootAccountRealm.class);
        add(auth, SessionAuthenticator.NAME, SessionAuthenticator.class);
        add(auth, HTTPHeaderAuthenticationRealm.NAME, HTTPHeaderAuthenticationRealm.class);
        add(auth, UsernamePasswordRealm.NAME, UsernamePasswordRealm.class);
        add(auth, BearerTokenRealm.NAME, BearerTokenRealm.class);
    }

    private void add(MapBinder<String, AuthenticatingRealm> auth, String name,
                     Class<? extends AuthenticatingRealm> realm) {
        if (!deactivatedRealms.contains(name)) {
            auth.addBinding(name).to(realm).in(Scopes.SINGLETON);
        }
    }
}
