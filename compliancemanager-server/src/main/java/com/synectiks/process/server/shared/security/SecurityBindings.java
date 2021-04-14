/*
 * */
package com.synectiks.process.server.shared.security;

import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.OptionalBinder;
import com.synectiks.process.server.plugin.PluginModule;
import com.synectiks.process.server.rest.models.system.sessions.responses.DefaultSessionResponseFactory;
import com.synectiks.process.server.rest.models.system.sessions.responses.SessionResponseFactory;
import com.synectiks.process.server.security.DefaultX509TrustManager;
import com.synectiks.process.server.security.TrustManagerProvider;
import com.synectiks.process.server.security.UserSessionTerminationListener;
import com.synectiks.process.server.security.encryption.EncryptedValueService;

import javax.net.ssl.TrustManager;

public class SecurityBindings extends PluginModule {
    @Override
    protected void configure() {
        bind(EncryptedValueService.class).asEagerSingleton();
        bind(UserSessionTerminationListener.class).asEagerSingleton();
        bind(Permissions.class).asEagerSingleton();
        bind(SessionCreator.class).in(Scopes.SINGLETON);
        addPermissions(RestPermissions.class);

        install(new FactoryModuleBuilder()
                .implement(TrustManager.class, DefaultX509TrustManager.class)
                .build(TrustManagerProvider.class));

        OptionalBinder.newOptionalBinder(binder(), ActorAwareAuthenticationTokenFactory.class)
                .setDefault().to(ActorAwareUsernamePasswordTokenFactory.class);
        OptionalBinder.newOptionalBinder(binder(), SessionResponseFactory.class)
                .setDefault().to(DefaultSessionResponseFactory.class);
    }
}
