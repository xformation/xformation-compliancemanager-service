/*
 * */
package com.synectiks.process.server.migrations.V20200803120800_GrantsMigrations;

import com.google.common.collect.Sets;
import com.synectiks.process.common.grn.GRNRegistry;
import com.synectiks.process.common.grn.GRNType;
import com.synectiks.process.common.security.Capability;
import com.synectiks.process.common.security.DBGrantService;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.shared.users.Role;
import com.synectiks.process.server.shared.users.UserService;
import com.synectiks.process.server.users.RoleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

import static com.synectiks.process.server.migrations.V20200803120800_GrantsMigrations.GrantsMetaMigration.MIGRATION_MAP;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RolesToGrantsMigration {
    private static final Logger LOG = LoggerFactory.getLogger(RolesToGrantsMigration.class);
    private final RoleService roleService;
    private final UserService userService;
    private final DBGrantService dbGrantService;
    private final GRNRegistry grnRegistry;
    private final String rootUsername;

    public RolesToGrantsMigration(RoleService roleService,
                                  UserService userService,
                                  DBGrantService dbGrantService,
                                  GRNRegistry grnRegistry,
                                  @Named("root_username") String rootUsername) {
        this.roleService = roleService;
        this.userService = userService;
        this.dbGrantService = dbGrantService;
        this.grnRegistry = grnRegistry;
        this.rootUsername = rootUsername;
    }

    public void upgrade() {
        final Set<MigratableRole> migratableRoles = findMigratableRoles();

        migratableRoles.forEach(migratableRole -> {
            final Role role = migratableRole.role;

            final Set<String> migratedPermissions = migrateRoleToGrant(migratableRole);

            if (role.getPermissions().removeAll(migratedPermissions)) {
                LOG.debug("Updating role <{}> new permissions: <{}>", role.getName(), role.getPermissions());

                if (role.getPermissions().isEmpty()) {
                    LOG.info("Removing the now empty role <{}>", role.getName());
                    userService.dissociateAllUsersFromRole(role);
                    roleService.delete(role.getName());
                } else {
                    try {
                        roleService.save(role);
                    } catch (ValidationException e) {
                        LOG.error("Failed to update modified role <{}>", role.getName(), e);
                    }
                }
            }
        });
    }

    private Set<String> migrateRoleToGrant(MigratableRole migratableRole) {
        final Set<String> migratedRolePermissions = new HashSet<>();
        final Collection<User> allRoleUsers = userService.loadAllForRole(migratableRole.role);

        migratableRole.migratableEntities.forEach((entityID, permissions) -> {
            final GrantsMetaMigration.GRNTypeCapability grnTypeCapability = MIGRATION_MAP.get(permissions);

            // Permissions are mappable to a grant
            if (grnTypeCapability != null) {
                final Capability capability = grnTypeCapability.capability;
                final GRNType grnType = grnTypeCapability.grnType;
                allRoleUsers.forEach(user -> {
                    dbGrantService.ensure(grnRegistry.ofUser(user), capability, grnType.toGRN(entityID), rootUsername);
                    LOG.info("Migrating entity <{}> permissions <{}> to <{}> grant for user <{}>", grnType.toGRN(entityID), permissions, capability, user.getName());
                });
                migratedRolePermissions.addAll(permissions.stream().map(p -> p + ":" + entityID).collect(Collectors.toSet()));
            } else {
                LOG.info("Skipping non-migratable entity <{}>. Permissions <{}> cannot be converted to a grant capability", entityID, permissions);
            }
        });
        return migratedRolePermissions;
    }

    private Set<MigratableRole> findMigratableRoles() {
        final Set<MigratableRole> migratableRoles = new HashSet<>();

        final Set<Role> roles = roleService.loadAll();
        roles.forEach(role -> {
            final Map<String, Set<String>> migratableIds = new HashMap<>();

            // Inspect all permissions that are made of 3 parts and don't contain multiple subparts
            role.getPermissions().stream().map(GrantsMetaMigration.MigrationWildcardPermission::new)
                    .filter(p -> p.getParts().size() == 3 && p.getParts().stream().allMatch(part -> part.size() == 1))
                    .forEach(p -> {
                        String permissionType = p.subPart(0);
                        String restPermission = p.subPart(0) + ":" + p.subPart(1);
                        String id = p.subPart(2);

                        if (MIGRATION_MAP.keySet().stream().flatMap(Collection::stream).anyMatch(perm -> perm.startsWith(permissionType + ":"))) {
                            LOG.debug("Potentially migratable role <{}> permission <{}> id <{}>", role.getName(), restPermission, id);
                            if (migratableIds.containsKey(id)) {
                                migratableIds.get(id).add(restPermission);
                            } else {
                                migratableIds.put(id, Sets.newHashSet(restPermission));
                            }
                        }
                    });
            if (!migratableIds.isEmpty()) {
                migratableRoles.add(new MigratableRole(role, migratableIds));
            }
        });
        LOG.debug("migratableRoles <{}>", migratableRoles);
        return migratableRoles;
    }

    private static class MigratableRole {
        Role role;
        Map<String, Set<String>> migratableEntities;

        public MigratableRole(Role role, Map<String, Set<String>> migratableEntities) {
            this.role = role;
            this.migratableEntities = migratableEntities;
        }

        @Override
        public String toString() {
            return "MigratableRole{" +
                    "roleID='" + role.getId() + '\'' +
                    ", migratableIds=" + migratableEntities +
                    '}';
        }
    }
}
