/*
 * */
package com.synectiks.process.server.migrations.V20200803120800_GrantsMigrations;

import com.google.common.collect.ImmutableSet;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNRegistry;
import com.synectiks.process.common.grn.GRNTypes;
import com.synectiks.process.common.plugins.views.search.views.ViewRequirements;
import com.synectiks.process.common.plugins.views.search.views.ViewService;
import com.synectiks.process.common.security.Capability;
import com.synectiks.process.common.security.DBGrantService;
import com.synectiks.process.common.security.entities.EntityOwnershipService;
import com.synectiks.process.common.testing.GRNExtension;
import com.synectiks.process.common.testing.mongodb.MongoDBExtension;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBTestService;
import com.synectiks.process.common.testing.mongodb.MongoJackExtension;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.migrations.V20200803120800_GrantsMigrations.ViewSharingToGrantsMigration;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.shared.users.Role;
import com.synectiks.process.server.shared.users.UserService;
import com.synectiks.process.server.users.RoleImpl;
import com.synectiks.process.server.users.RoleService;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MongoDBExtension.class)
@ExtendWith(MongoJackExtension.class)
@ExtendWith(GRNExtension.class)
@ExtendWith(MockitoExtension.class)
@MongoDBFixtures("view-sharings.json")
class ViewSharingToGrantsMigrationTest {
    private ViewSharingToGrantsMigration migration;
    private DBGrantService grantService;
    private UserService userService;
    private RoleService roleService;
    private MongoCollection<Document> dbCollection;

    @BeforeEach
    void setUp(MongoDBTestService mongodb,
               MongoJackObjectMapperProvider objectMapperProvider,
               GRNRegistry grnRegistry,
               @Mock ClusterConfigService clusterConfigService,
               @Mock UserService userService,
               @Mock RoleService roleService) {

        this.dbCollection = mongodb.mongoCollection("view_sharings");
        this.userService = userService;
        this.roleService = roleService;
        this.grantService = new DBGrantService(mongodb.mongoConnection(), objectMapperProvider, grnRegistry);

        when(userService.load(anyString())).thenAnswer(a -> {
            final String argument = a.getArgument(0);
            return createUser(argument);
        });

        final EntityOwnershipService entityOwnershipService = new EntityOwnershipService(grantService, grnRegistry);
        final TestViewService viewService = new TestViewService(mongodb.mongoConnection(), objectMapperProvider, clusterConfigService, entityOwnershipService);

        this.migration = new ViewSharingToGrantsMigration(mongodb.mongoConnection(), grantService, userService, roleService, "admin", viewService, grnRegistry);
    }

    private void assertDeletedViewSharing(String id) {
        assertThat(dbCollection.countDocuments(Filters.eq("_id", new ObjectId(id))))
                .isEqualTo(0);
    }

    @Test
    @DisplayName("migrate user shares")
    void migrateUserShares() throws Exception {
        final GRN jane = GRNTypes.USER.toGRN("jane");
        final GRN john = GRNTypes.USER.toGRN("john");
        final GRN search = GRNTypes.SEARCH.toGRN("54e3deadbeefdeadbeef0001");

        when(roleService.load(anyString())).thenThrow(new NotFoundException());

        assertThat(grantService.hasGrantFor(jane, Capability.VIEW, search)).isFalse();
        assertThat(grantService.hasGrantFor(john, Capability.VIEW, search)).isFalse();

        migration.upgrade();

        assertThat(grantService.hasGrantFor(jane, Capability.VIEW, search)).isTrue();
        assertThat(grantService.hasGrantFor(john, Capability.VIEW, search)).isTrue();

        assertThat(grantService.hasGrantFor(jane, Capability.OWN, search)).isFalse();
        assertThat(grantService.hasGrantFor(jane, Capability.MANAGE, search)).isFalse();
        assertThat(grantService.hasGrantFor(john, Capability.OWN, search)).isFalse();
        assertThat(grantService.hasGrantFor(john, Capability.MANAGE, search)).isFalse();

        assertDeletedViewSharing("54e3deadbeefdeadbeef0001");
    }

    @Test
    @DisplayName("migrate role shares")
    void migrateRoleShares() throws Exception {
        final User userJane = createUser("jane");
        final User userJohn = createUser("john");
        final Role role1 = createRole("role1");
        final Role role2 = createRole("role2");

        when(userService.loadAllForRole(role1)).thenReturn(ImmutableSet.of(userJane, userJohn));
        when(userService.loadAllForRole(role2)).thenReturn(Collections.emptySet());
        when(roleService.load(role1.getName())).thenReturn(role1);
        when(roleService.load(role2.getName())).thenReturn(role2);

        final GRN jane = GRNTypes.USER.toGRN(userJane.getName());
        final GRN john = GRNTypes.USER.toGRN(userJohn.getName());
        final GRN dashboard1 = GRNTypes.DASHBOARD.toGRN("54e3deadbeefdeadbeef0002");

        assertThat(grantService.hasGrantFor(jane, Capability.VIEW, dashboard1)).isFalse();
        assertThat(grantService.hasGrantFor(john, Capability.VIEW, dashboard1)).isFalse();

        migration.upgrade();

        assertThat(grantService.hasGrantFor(jane, Capability.VIEW, dashboard1)).isTrue();
        assertThat(grantService.hasGrantFor(john, Capability.VIEW, dashboard1)).isTrue();

        assertThat(grantService.hasGrantFor(jane, Capability.OWN, dashboard1)).isFalse();
        assertThat(grantService.hasGrantFor(jane, Capability.MANAGE, dashboard1)).isFalse();
        assertThat(grantService.hasGrantFor(john, Capability.OWN, dashboard1)).isFalse();
        assertThat(grantService.hasGrantFor(john, Capability.MANAGE, dashboard1)).isFalse();

        assertDeletedViewSharing("54e3deadbeefdeadbeef0002");
    }

    @Test
    @DisplayName("migrate all-of-instance shares")
    void migrateAllOfInstanceShares() throws Exception {

        final GRN everyone = GRNRegistry.GLOBAL_USER_GRN;
        when(roleService.load(anyString())).thenThrow(new NotFoundException());

        final GRN dashboard2 = GRNTypes.DASHBOARD.toGRN("54e3deadbeefdeadbeef0003");

        assertThat(grantService.hasGrantFor(everyone, Capability.VIEW, dashboard2)).isFalse();

        migration.upgrade();

        assertThat(grantService.hasGrantFor(everyone, Capability.VIEW, dashboard2)).isTrue();

        assertThat(grantService.hasGrantFor(everyone, Capability.OWN, dashboard2)).isFalse();
        assertThat(grantService.hasGrantFor(everyone, Capability.MANAGE, dashboard2)).isFalse();

        assertDeletedViewSharing("54e3deadbeefdeadbeef0003");
    }

    private User createUser(String name) {
        final User user = mock(User.class);
        lenient().when(user.getName()).thenReturn(name);
        lenient().when(user.getId()).thenReturn(name);
        return user;
    }

    private Role createRole(String name) {
        final RoleImpl role = new RoleImpl();

        role._id = new ObjectId().toHexString();
        role.setName(name);
        role.setNameLower(name.toLowerCase(Locale.US));
        role.setDescription("This is role: " + name);

        return role;
    }

    public static class TestViewService extends ViewService {
        public TestViewService(MongoConnection mongoConnection,
                               MongoJackObjectMapperProvider mapper,
                               ClusterConfigService clusterConfigService,
                               EntityOwnershipService entityOwnerShipService) {
            super(mongoConnection, mapper, clusterConfigService, view -> new ViewRequirements(Collections.emptySet(), view), entityOwnerShipService);
        }
    }
}
