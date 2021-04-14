/*
 * */
package com.synectiks.process.common.plugins.sidecar;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.synectiks.process.common.plugins.sidecar.audit.SidecarAuditEventTypes;
import com.synectiks.process.common.plugins.sidecar.common.SidecarPluginConfiguration;
import com.synectiks.process.common.plugins.sidecar.filter.AdministrationFilter;
import com.synectiks.process.common.plugins.sidecar.filter.CollectorAdministrationFilter;
import com.synectiks.process.common.plugins.sidecar.filter.ConfigurationAdministrationFilter;
import com.synectiks.process.common.plugins.sidecar.filter.OsAdministrationFilter;
import com.synectiks.process.common.plugins.sidecar.filter.StatusAdministrationFilter;
import com.synectiks.process.common.plugins.sidecar.migrations.V20180212165000_AddDefaultCollectors;
import com.synectiks.process.common.plugins.sidecar.migrations.V20180323150000_AddSidecarUser;
import com.synectiks.process.common.plugins.sidecar.migrations.V20180601151500_AddDefaultConfiguration;
import com.synectiks.process.common.plugins.sidecar.periodical.PurgeExpiredConfigurationUploads;
import com.synectiks.process.common.plugins.sidecar.periodical.PurgeExpiredSidecarsThread;
import com.synectiks.process.common.plugins.sidecar.permissions.SidecarRestPermissions;
import com.synectiks.process.common.plugins.sidecar.services.CollectorService;
import com.synectiks.process.common.plugins.sidecar.services.ConfigurationService;
import com.synectiks.process.common.plugins.sidecar.services.ConfigurationVariableService;
import com.synectiks.process.common.plugins.sidecar.services.EtagService;
import com.synectiks.process.common.plugins.sidecar.services.SidecarService;
import com.synectiks.process.server.migrations.Migration;
import com.synectiks.process.server.plugin.PluginConfigBean;
import com.synectiks.process.server.plugin.PluginModule;

import java.util.Set;

public class SidecarModule extends PluginModule {
    @Override
    public Set<? extends PluginConfigBean> getConfigBeans() {
        return ImmutableSet.of(
                new SidecarPluginConfiguration()
        );
    }

    @Override
    protected void configure() {
        bind(ConfigurationService.class).asEagerSingleton();
        bind(SidecarService.class).asEagerSingleton();
        bind(CollectorService.class).asEagerSingleton();
        bind(ConfigurationVariableService.class).asEagerSingleton();

        install(new FactoryModuleBuilder()
                .implement(AdministrationFilter.class, Names.named("collector"), CollectorAdministrationFilter.class)
                .implement(AdministrationFilter.class, Names.named("configuration"), ConfigurationAdministrationFilter.class)
                .implement(AdministrationFilter.class, Names.named("os"), OsAdministrationFilter.class)
                .implement(AdministrationFilter.class, Names.named("status"), StatusAdministrationFilter.class)
                .build(AdministrationFilter.Factory.class));

        registerRestControllerPackage(getClass().getPackage().getName());
        addPermissions(SidecarRestPermissions.class);
        addPeriodical(PurgeExpiredSidecarsThread.class);
        addPeriodical(PurgeExpiredConfigurationUploads.class);

        addAuditEventTypes(SidecarAuditEventTypes.class);

        final Multibinder<Migration> binder = Multibinder.newSetBinder(binder(), Migration.class);
        binder.addBinding().to(V20180212165000_AddDefaultCollectors.class);
        binder.addBinding().to(V20180323150000_AddSidecarUser.class);
        binder.addBinding().to(V20180601151500_AddDefaultConfiguration.class);

        serviceBinder().addBinding().to(EtagService.class).in(Scopes.SINGLETON);
    }
}
