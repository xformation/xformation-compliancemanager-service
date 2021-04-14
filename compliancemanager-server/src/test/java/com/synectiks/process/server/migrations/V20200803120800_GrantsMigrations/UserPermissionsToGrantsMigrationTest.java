/*
 * */
package com.synectiks.process.server.migrations.V20200803120800_GrantsMigrations;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.grn.GRNRegistry;
import com.synectiks.process.common.plugins.views.search.views.ViewDTO;
import com.synectiks.process.common.plugins.views.search.views.ViewService;
import com.synectiks.process.common.security.Capability;
import com.synectiks.process.common.security.DBGrantService;
import com.synectiks.process.common.security.GrantDTO;
import com.synectiks.process.common.testing.GRNExtension;
import com.synectiks.process.common.testing.TestUserService;
import com.synectiks.process.common.testing.TestUserServiceExtension;
import com.synectiks.process.common.testing.mongodb.MongoDBExtension;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBTestService;
import com.synectiks.process.common.testing.mongodb.MongoJackExtension;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.migrations.V20200803120800_GrantsMigrations.UserPermissionsToGrantsMigration;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.shared.security.Permissions;
import com.synectiks.process.server.shared.users.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MongoDBExtension.class)
@ExtendWith(MongoJackExtension.class)
@ExtendWith(MockitoExtension.class)
@ExtendWith(GRNExtension.class)
@ExtendWith(TestUserServiceExtension.class)
@MongoDBFixtures("MigrateUserPermissionsToGrantsTest.json")
class UserPermissionsToGrantsMigrationTest {
    private UserPermissionsToGrantsMigration migration;
    private DBGrantService dbGrantService;
    private GRNRegistry grnRegistry;
    private UserService userService;
    private int userSelfEditPermissionCount;
    private ViewService viewService;

    @BeforeEach
    void setUp(MongoDBTestService mongodb,
               MongoJackObjectMapperProvider mongoJackObjectMapperProvider,
               GRNRegistry grnRegistry,
               TestUserService userService,
               @Mock ViewService viewService) {

        this.grnRegistry = grnRegistry;
        this.viewService = viewService;

        this.userSelfEditPermissionCount = new Permissions(ImmutableSet.of()).userSelfEditPermissions("dummy").size();

        dbGrantService = new DBGrantService(mongodb.mongoConnection(), mongoJackObjectMapperProvider, grnRegistry);
        this.userService = userService;
        DBGrantService dbGrantService = new DBGrantService(mongodb.mongoConnection(), mongoJackObjectMapperProvider, grnRegistry);
        migration = new UserPermissionsToGrantsMigration(userService, dbGrantService, grnRegistry, viewService, "admin");
    }

    @Test
    void migrateAllUserPermissions() {
        final ViewDTO view1 = mock(ViewDTO.class);
        final ViewDTO view2 = mock(ViewDTO.class);

        when(view1.type()).thenReturn(ViewDTO.Type.DASHBOARD);
        when(view2.type()).thenReturn(ViewDTO.Type.SEARCH);
        when(viewService.get("5c40ad603c034441a56943be")).thenReturn(Optional.of(view1));
        when(viewService.get("5c40ad603c034441a56943c0")).thenReturn(Optional.of(view2));

        User testuser1 = userService.load("testuser1");
        assertThat(testuser1).isNotNull();

        assertThat(testuser1.getPermissions().size()).isEqualTo(11 + userSelfEditPermissionCount);
        assertThat(dbGrantService.getForGranteesOrGlobal(ImmutableSet.of(grnRegistry.ofUser(testuser1)))).isEmpty();

        migration.upgrade();

        // check created grants for testuser1
        final ImmutableSet<GrantDTO> grants = dbGrantService.getForGranteesOrGlobal(ImmutableSet.of(grnRegistry.ofUser(testuser1)));
        assertGrantInSet(grants, "grn::::dashboard:5e2afc66cd19517ec2dabadd", Capability.VIEW);
        assertGrantInSet(grants, "grn::::dashboard:5e2afc66cd19517ec2dabadf", Capability.MANAGE);
        assertGrantInSet(grants, "grn::::stream:5c40ad603c034441a56942bd", Capability.VIEW);
        assertGrantInSet(grants, "grn::::stream:5e2f5cfb4868e67ad4da562d", Capability.VIEW);
        assertGrantInSet(grants, "grn::::dashboard:5c40ad603c034441a56943be", Capability.MANAGE);
        assertGrantInSet(grants, "grn::::search:5c40ad603c034441a56943c0", Capability.VIEW);
        assertGrantInSet(grants, "grn::::event_definition:5c40ad603c034441a56942bf", Capability.MANAGE);
        assertGrantInSet(grants, "grn::::event_definition:5c40ad603c034441a56942c0", Capability.VIEW);
        assertThat(grants.size()).isEqualTo(8);

        // reload user and check that all migrated permissions have been removed
        testuser1 = userService.load("testuser1");
        assertThat(testuser1).isNotNull();
        assertThat(testuser1.getPermissions().size()).isEqualTo(userSelfEditPermissionCount);
    }

    @Test
    void migrateSomeUserPermissions() {

        User testuser2 = userService.load("testuser2");
        assertThat(testuser2).isNotNull();

        assertThat(testuser2.getPermissions().size()).isEqualTo(6 + userSelfEditPermissionCount);
        assertThat(dbGrantService.getForGranteesOrGlobal(ImmutableSet.of(grnRegistry.ofUser(testuser2)))).isEmpty();

        migration.upgrade();

        // check created grants for testuser2
        final ImmutableSet<GrantDTO> grants = dbGrantService.getForGranteesOrGlobal(ImmutableSet.of(grnRegistry.ofUser(testuser2)));
        assertGrantInSet(grants, "grn::::dashboard:5e2afc66cd19517ec2dabadf", Capability.MANAGE);
        assertThat(grants.size()).isEqualTo(1);

        // reload user and check that all migrated permissions have been removed. (should be only two less)
        testuser2 = userService.load("testuser2");
        assertThat(testuser2).isNotNull();
        assertThat(testuser2.getPermissions().size()).isEqualTo(4 + userSelfEditPermissionCount);
    }


    private void assertGrantInSet(Set<GrantDTO> grants, String target, Capability capability) {
        assertThat(grants.stream().anyMatch(g -> g.target().equals(grnRegistry.parse(target)) && g.capability().equals(capability))).isTrue();
    }
}
