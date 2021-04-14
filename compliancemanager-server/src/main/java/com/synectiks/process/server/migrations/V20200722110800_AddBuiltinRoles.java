/*
 * */
package com.synectiks.process.server.migrations;

import javax.inject.Inject;

import com.synectiks.process.server.plugin.security.PluginPermissions;

import java.time.ZonedDateTime;
import java.util.Set;

public class V20200722110800_AddBuiltinRoles extends Migration {
    private final MigrationHelpers helpers;
    private final Set<PluginPermissions> pluginPermissions;

    @Inject
    public V20200722110800_AddBuiltinRoles(MigrationHelpers helpers,
                                           Set<PluginPermissions> pluginPermissions) {
        this.helpers = helpers;
        this.pluginPermissions = pluginPermissions;
    }

    @Override
    public ZonedDateTime createdAt() {
        return ZonedDateTime.parse("2020-07-22T11:08:00Z");
    }

    @Override
    public void upgrade() {
        for (PluginPermissions permission: pluginPermissions) {
            permission.builtinRoles().forEach(r -> helpers.ensureBuiltinRole(r.name(), r.description(), r.permissions()));
        }
    }
}
