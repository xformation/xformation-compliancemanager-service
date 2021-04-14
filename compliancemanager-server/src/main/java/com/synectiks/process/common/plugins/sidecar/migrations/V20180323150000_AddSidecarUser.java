/*
 * */
package com.synectiks.process.common.plugins.sidecar.migrations;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.synectiks.process.common.plugins.sidecar.common.SidecarPluginConfiguration;
import com.synectiks.process.common.plugins.sidecar.permissions.SidecarRestPermissions;
import com.synectiks.process.server.migrations.Migration;
import com.synectiks.process.server.migrations.MigrationHelpers;
import com.synectiks.process.server.users.RoleService;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.UUID;

public class V20180323150000_AddSidecarUser extends Migration {
    private final RoleService roleService;
    private final String sidecarUser;
    private final MigrationHelpers helpers;

    @Inject
    public V20180323150000_AddSidecarUser(SidecarPluginConfiguration pluginConfiguration,
                                          RoleService roleService,
                                          MigrationHelpers migrationHelpers) {
        this.roleService = roleService;
        this.sidecarUser = pluginConfiguration.getUser();
        this.helpers = migrationHelpers;
    }

    @Override
    public ZonedDateTime createdAt() {
        return ZonedDateTime.parse("2018-03-23T15:00:00Z");
    }

    @Override
    public void upgrade() {
        final String roleId = helpers.ensureBuiltinRole(
                "Sidecar System (Internal)",
                "Internal technical role. Grants access to register and pull configurations for a Sidecar node (built-in)",
                ImmutableSet.of(
                        SidecarRestPermissions.COLLECTORS_READ,
                        SidecarRestPermissions.CONFIGURATIONS_READ,
                        SidecarRestPermissions.SIDECARS_UPDATE));

        helpers.ensureUser(
                sidecarUser,
                UUID.randomUUID().toString(),
                "Sidecar System User (built-in)",
                "sidecar@alertmanager.local",
                Sets.newHashSet(
                        roleId,
                        roleService.getReaderRoleObjectId()));
    }
}
