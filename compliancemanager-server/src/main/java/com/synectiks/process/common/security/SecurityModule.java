/*
 * */
package com.synectiks.process.common.security;

import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.OptionalBinder;
import com.synectiks.process.common.security.authservice.AuthServiceBackend;
import com.synectiks.process.common.security.authservice.InternalAuthServiceBackend;
import com.synectiks.process.common.security.authservice.ProvisionerAction;
import com.synectiks.process.common.security.authservice.backend.ADAuthServiceBackend;
import com.synectiks.process.common.security.authservice.backend.ADAuthServiceBackendConfig;
import com.synectiks.process.common.security.authservice.backend.LDAPAuthServiceBackend;
import com.synectiks.process.common.security.authservice.backend.LDAPAuthServiceBackendConfig;
import com.synectiks.process.common.security.authservice.backend.MongoDBAuthServiceBackend;
import com.synectiks.process.common.security.authservice.ldap.UnboundLDAPConnector;
import com.synectiks.process.common.security.shares.DefaultGranteeService;
import com.synectiks.process.common.security.shares.GranteeService;
import com.synectiks.process.server.plugin.PluginModule;

public class SecurityModule extends PluginModule {
    @Override
    protected void configure() {
        // Call the following to ensure the presence of the multi binder and avoid startup errors when no action is registered
        MapBinder.newMapBinder(
                binder(),
                TypeLiteral.get(String.class),
                new TypeLiteral<ProvisionerAction.Factory<? extends ProvisionerAction>>() {}
        );
        authServiceBackendBinder();

        bind(BuiltinCapabilities.class).asEagerSingleton();
        bind(UnboundLDAPConnector.class).in(Scopes.SINGLETON);

        install(new FactoryModuleBuilder().implement(GranteeAuthorizer.class, GranteeAuthorizer.class).build(GranteeAuthorizer.Factory.class));

        OptionalBinder.newOptionalBinder(binder(), PermissionAndRoleResolver.class)
                .setDefault().to(DefaultPermissionAndRoleResolver.class);

        OptionalBinder.newOptionalBinder(binder(), GranteeService.class)
                .setDefault().to(DefaultGranteeService.class);

        bind(AuthServiceBackend.class).annotatedWith(InternalAuthServiceBackend.class).to(MongoDBAuthServiceBackend.class);

        // Add all rest resources in this package
        // TODO: Check if we need to use addRestResource() here for the final version to make sure
        //       we get the path prefix. Do we want this?
        registerRestControllerPackage(getClass().getPackage().getName());

        addAuditEventTypes(SecurityAuditEventTypes.class);

        addAuthServiceBackend(LDAPAuthServiceBackend.TYPE_NAME,
                LDAPAuthServiceBackend.class,
                LDAPAuthServiceBackend.Factory.class,
                LDAPAuthServiceBackendConfig.class);
        addAuthServiceBackend(ADAuthServiceBackend.TYPE_NAME,
                ADAuthServiceBackend.class,
                ADAuthServiceBackend.Factory.class,
                ADAuthServiceBackendConfig.class);
    }
}
