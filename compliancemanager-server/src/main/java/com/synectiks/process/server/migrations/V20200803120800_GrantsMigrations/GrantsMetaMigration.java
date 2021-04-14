/*
 * */
package com.synectiks.process.server.migrations.V20200803120800_GrantsMigrations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.synectiks.process.common.grn.GRNRegistry;
import com.synectiks.process.common.grn.GRNType;
import com.synectiks.process.common.plugins.views.search.views.ViewService;
import com.synectiks.process.common.security.Capability;
import com.synectiks.process.common.security.DBGrantService;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.migrations.Migration;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.shared.users.UserService;
import com.synectiks.process.server.users.RoleService;

import org.apache.shiro.authz.permission.WildcardPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.synectiks.process.common.grn.GRNTypes.DASHBOARD;
import static com.synectiks.process.common.grn.GRNTypes.EVENT_DEFINITION;
import static com.synectiks.process.common.grn.GRNTypes.STREAM;
import static com.synectiks.process.common.plugins.views.search.rest.ViewsRestPermissions.VIEW_EDIT;
import static com.synectiks.process.common.plugins.views.search.rest.ViewsRestPermissions.VIEW_READ;
import static com.synectiks.process.server.shared.security.RestPermissions.DASHBOARDS_EDIT;
import static com.synectiks.process.server.shared.security.RestPermissions.DASHBOARDS_READ;
import static com.synectiks.process.server.shared.security.RestPermissions.EVENT_DEFINITIONS_EDIT;
import static com.synectiks.process.server.shared.security.RestPermissions.EVENT_DEFINITIONS_READ;
import static com.synectiks.process.server.shared.security.RestPermissions.STREAMS_EDIT;
import static com.synectiks.process.server.shared.security.RestPermissions.STREAMS_READ;

public class GrantsMetaMigration extends Migration {
    private static final Logger LOG = LoggerFactory.getLogger(GrantsMetaMigration.class);
    private final RoleService roleService;
    private final UserService userService;
    private final DBGrantService dbGrantService;
    private final GRNRegistry grnRegistry;
    private final String rootUsername;
    private final MongoConnection mongoConnection;
    private final ViewService viewService;
    private final ClusterConfigService clusterConfigService;

    @Inject
    public GrantsMetaMigration(RoleService roleService,
                               UserService userService,
                               DBGrantService dbGrantService,
                               GRNRegistry grnRegistry,
                               @Named("root_username") String rootUsername,
                               MongoConnection mongoConnection,
                               ViewService viewService,
                               ClusterConfigService clusterConfigService) {
        this.roleService = roleService;
        this.userService = userService;
        this.dbGrantService = dbGrantService;
        this.grnRegistry = grnRegistry;
        this.rootUsername = rootUsername;
        this.mongoConnection = mongoConnection;
        this.viewService = viewService;
        this.clusterConfigService = clusterConfigService;
    }

    @Override
    public ZonedDateTime createdAt() {
        return ZonedDateTime.parse("2020-08-03T12:08:00Z");
    }

    public static final Map<Set<String>, GRNTypeCapability> MIGRATION_MAP = ImmutableMap.<Set<String>, GRNTypeCapability>builder()
            .put(ImmutableSet.of(DASHBOARDS_READ, DASHBOARDS_EDIT), new GRNTypeCapability(DASHBOARD, Capability.MANAGE))
            .put(ImmutableSet.of(DASHBOARDS_READ), new GRNTypeCapability(DASHBOARD, Capability.VIEW))
            .put(ImmutableSet.of(STREAMS_READ, STREAMS_EDIT), new GRNTypeCapability(STREAM, Capability.MANAGE))
            .put(ImmutableSet.of(STREAMS_READ), new GRNTypeCapability(STREAM, Capability.VIEW))
            .put(ImmutableSet.of(VIEW_READ, VIEW_EDIT), new GRNTypeCapability(null, Capability.MANAGE))
            .put(ImmutableSet.of(VIEW_READ), new GRNTypeCapability(null, Capability.VIEW))
            .put(ImmutableSet.of(EVENT_DEFINITIONS_READ, EVENT_DEFINITIONS_EDIT), new GRNTypeCapability(EVENT_DEFINITION, Capability.MANAGE))
            .put(ImmutableSet.of(EVENT_DEFINITIONS_READ), new GRNTypeCapability(EVENT_DEFINITION, Capability.VIEW))
            .build();

    @Override
    public void upgrade() {
        if (clusterConfigService.get(MigrationCompleted.class) != null) {
            LOG.debug("Migration already completed.");
            return;
        }
        // ViewSharingToGrantsMigration needs to run before the RolesToGrantsMigration drops empty roles
        new ViewSharingToGrantsMigration(mongoConnection, dbGrantService, userService, roleService, rootUsername, viewService, grnRegistry).upgrade();
        new RolesToGrantsMigration(roleService, userService, dbGrantService, grnRegistry, rootUsername).upgrade();
        new ViewOwnerShipToGrantsMigration(userService, dbGrantService, rootUsername, viewService, grnRegistry).upgrade();
        new UserPermissionsToGrantsMigration(userService, dbGrantService, grnRegistry, viewService, rootUsername).upgrade();

        this.clusterConfigService.write(MigrationCompleted.create());
    }

    public static class GRNTypeCapability {
        final GRNType grnType;
        final Capability capability;

        public GRNTypeCapability(GRNType grnType, Capability capability) {
            this.grnType = grnType;
            this.capability = capability;
        }
    }

    // only needed to access protected getParts() method from WildcardPermission
    public static class MigrationWildcardPermission extends WildcardPermission {
        public MigrationWildcardPermission(String wildcardString) {
            super(wildcardString);
        }

        @Override
        protected List<Set<String>> getParts() {
            return super.getParts();
        }

        protected String subPart(int idx) {
            return Iterables.getOnlyElement(getParts().get(idx));
        }
    }

    @JsonAutoDetect
    @AutoValue
    public static abstract class MigrationCompleted {
        @JsonCreator
        public static MigrationCompleted create() {
            return new AutoValue_GrantsMetaMigration_MigrationCompleted();
        }
    }
}
