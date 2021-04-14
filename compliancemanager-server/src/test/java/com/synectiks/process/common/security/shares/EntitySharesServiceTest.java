/*
 * */
package com.synectiks.process.common.security.shares;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNRegistry;
import com.synectiks.process.common.grn.GRNTypes;
import com.synectiks.process.common.security.BuiltinCapabilities;
import com.synectiks.process.common.security.Capability;
import com.synectiks.process.common.security.DBGrantService;
import com.synectiks.process.common.security.GrantDTO;
import com.synectiks.process.common.security.entities.EntityDependencyPermissionChecker;
import com.synectiks.process.common.security.entities.EntityDependencyResolver;
import com.synectiks.process.common.security.shares.EntityShareRequest;
import com.synectiks.process.common.security.shares.EntityShareResponse;
import com.synectiks.process.common.security.shares.EntitySharesService;
import com.synectiks.process.common.security.shares.GranteeService;
import com.synectiks.process.common.testing.GRNExtension;
import com.synectiks.process.common.testing.mongodb.MongoDBExtension;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBTestService;
import com.synectiks.process.common.testing.mongodb.MongoJackExtension;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.plugin.database.users.User;

import org.apache.shiro.subject.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MongoDBExtension.class)
@ExtendWith(MongoJackExtension.class)
@ExtendWith(GRNExtension.class)
@ExtendWith(MockitoExtension.class)
@MongoDBFixtures("EntitySharesServiceTest.json")
public class EntitySharesServiceTest {

    private EntitySharesService entitySharesService;

    @Mock
    private EntityDependencyResolver entityDependencyResolver;

    @Mock
    private EntityDependencyPermissionChecker entityDependencyPermissionChecker;

    @Mock
    private GranteeService granteeService;

    private GRNRegistry grnRegistry;
    private DBGrantService dbGrantService;

    @BeforeEach
    void setUp(MongoDBTestService mongodb,
               MongoJackObjectMapperProvider mongoJackObjectMapperProvider,
               GRNRegistry grnRegistry) {
        this.grnRegistry = grnRegistry;

        dbGrantService = new DBGrantService(mongodb.mongoConnection(), mongoJackObjectMapperProvider, this.grnRegistry);

        lenient().when(entityDependencyResolver.resolve(any())).thenReturn(ImmutableSet.of());
        lenient().when(entityDependencyPermissionChecker.check(any(), any(), any())).thenReturn(ImmutableMultimap.of());
        lenient().when(granteeService.getAvailableGrantees(any())).thenReturn(ImmutableSet.of());

        final EventBus serverEventBus = mock(EventBus.class);
        this.entitySharesService = new EntitySharesService(dbGrantService, entityDependencyResolver, entityDependencyPermissionChecker, grnRegistry, granteeService, serverEventBus);

        // TODO this is needed to initialize the CAPABILITIES field
        new BuiltinCapabilities();
    }

    // TODO Test more EntitySharesService functionality

    @DisplayName("Validates we cannot remove the last owner")
    @Test
    void validateLastOwnerCannotBeRemoved() {
        final GRN entity = grnRegistry.newGRN(GRNTypes.STREAM, "54e3deadbeefdeadbeefaffe");
        final EntityShareRequest shareRequest = EntityShareRequest.create(ImmutableMap.of());

        // This test can also see the "invisible user"
        final Set<GRN> allGrantees = dbGrantService.getAll().stream().map(GrantDTO::grantee).collect(Collectors.toSet());
        lenient().when(granteeService.getAvailableGrantees(any())).thenReturn(
                allGrantees.stream().map(g -> EntityShareResponse.AvailableGrantee.create(g, "user", g.entity())).collect(Collectors.toSet())
        );

        final User user = createMockUser("hans");
        final Subject subject = mock(Subject.class);
        final EntityShareResponse entityShareResponse = entitySharesService.prepareShare(entity, shareRequest, user, subject);
        assertThat(entityShareResponse.validationResult()).satisfies(validationResult -> {
            assertThat(validationResult.failed()).isTrue();
            assertThat(validationResult.getErrors()).isNotEmpty();
            assertThat(validationResult.getErrors().get(EntityShareRequest.SELECTED_GRANTEE_CAPABILITIES).toString())
                    .contains("Removing the following owners")
                    .contains("grn::::user:jane")
                    .contains("grn::::user:invisible");
        });
    }

    @DisplayName("The validation should ignore invisble owners")
    @Test
    void ignoreInvisibleOwners() {
        final GRN entity = grnRegistry.newGRN(GRNTypes.STREAM, "54e3deadbeefdeadbeefaffe");
        final EntityShareRequest shareRequest = EntityShareRequest.create(ImmutableMap.of());

        final Set<GRN> allGrantees = dbGrantService.getAll().stream().map(GrantDTO::grantee).collect(Collectors.toSet());
        lenient().when(granteeService.getAvailableGrantees(any())).thenReturn(
                allGrantees.stream()
                        .filter(g -> g.toString().equals("grn::::user:invisible"))
                        .map(g -> EntityShareResponse.AvailableGrantee.create(g, "user", g.entity())).collect(Collectors.toSet())
        );

        final User user = createMockUser("hans");
        final Subject subject = mock(Subject.class);
        final EntityShareResponse entityShareResponse = entitySharesService.prepareShare(entity, shareRequest, user, subject);
        assertThat(entityShareResponse.validationResult()).satisfies(validationResult -> {
            assertThat(validationResult.failed()).isFalse();
        });
    }

    @DisplayName("Validates we cannot remove the last owner by changing the own capability")
    @Test
    void validateLastOwnerCannotBeRemovedByChangingCapability() {
        final GRN entity = grnRegistry.newGRN(GRNTypes.EVENT_DEFINITION, "54e3deadbeefdeadbeefaffe");
        final GRN bob = grnRegistry.newGRN(GRNTypes.USER, "bob");
        final EntityShareRequest shareRequest = EntityShareRequest.create(ImmutableMap.of(bob, Capability.VIEW));

        final User user = createMockUser("requestingUser");
        when(granteeService.getAvailableGrantees(user)).thenReturn(ImmutableSet.of(EntityShareResponse.AvailableGrantee.create(bob, "user", "bob")));
        final Subject subject = mock(Subject.class);
        final EntityShareResponse entityShareResponse = entitySharesService.prepareShare(entity, shareRequest, user, subject);
        assertThat(entityShareResponse.validationResult()).satisfies(validationResult -> {
            assertThat(validationResult.failed()).isTrue();
            assertThat(validationResult.getErrors()).isNotEmpty();
            assertThat(validationResult.getErrors().get(EntityShareRequest.SELECTED_GRANTEE_CAPABILITIES))
                    .contains("Removing the following owners <[grn::::user:bob]> will leave the entity ownerless.");
        });
    }

    @DisplayName("Validates we can switch owners")
    @Test
    void validateOwnerSwitch() {
        final GRN entity = grnRegistry.newGRN(GRNTypes.STREAM, "54e3deadbeefdeadbeefaffe");
        final GRN horst = grnRegistry.newGRN(GRNTypes.USER, "horst");
        final EntityShareRequest shareRequest = EntityShareRequest.create(ImmutableMap.of(horst, Capability.OWN));

        final User user = createMockUser("hans");
        final Subject subject = mock(Subject.class);
        final EntityShareResponse entityShareResponse = entitySharesService.prepareShare(entity, shareRequest, user, subject);
        assertThat(entityShareResponse.validationResult()).satisfies(validationResult -> {
            assertThat(validationResult.failed()).isFalse();
            assertThat(validationResult.getErrors()).isEmpty();
        });
    }

    @DisplayName("Validates we can modify ownerless entitites")
    @Test
    void validateOwnerless() {
        final GRN entity = grnRegistry.newGRN(GRNTypes.DASHBOARD, "54e3deadbeefdeadbeefaffe");
        final GRN horst = grnRegistry.newGRN(GRNTypes.USER, "horst");
        final EntityShareRequest shareRequest = EntityShareRequest.create(ImmutableMap.of(horst, Capability.MANAGE));

        final User user = createMockUser("hans");
        final Subject subject = mock(Subject.class);
        final EntityShareResponse entityShareResponse = entitySharesService.prepareShare(entity, shareRequest, user, subject);
        assertThat(entityShareResponse.validationResult()).satisfies(validationResult -> {
            assertThat(validationResult.failed()).isFalse();
            assertThat(validationResult.getErrors()).isEmpty();
        });
    }

    @DisplayName("Don't run validation on initial empty request")
    @Test
    void noValidationOnEmptyRequest() {
        final GRN entity = grnRegistry.newGRN(GRNTypes.DASHBOARD, "54e3deadbeefdeadbeefaffe");
        final EntityShareRequest shareRequest = EntityShareRequest.create(null);

        final User user = createMockUser("hans");
        final Subject subject = mock(Subject.class);
        final EntityShareResponse entityShareResponse = entitySharesService.prepareShare(entity, shareRequest, user, subject);
        assertThat(entityShareResponse.validationResult()).satisfies(validationResult -> {
            assertThat(validationResult.failed()).isFalse();
            assertThat(validationResult.getErrors()).isEmpty();
        });
    }

    @DisplayName("Only show shares for visible grantees")
    @Test
    void noSharesforInvisibleGrantees() {
        final GRN entity = grnRegistry.newGRN(GRNTypes.STREAM, "54e3deadbeefdeadbeefaffe");
        final EntityShareRequest shareRequest = EntityShareRequest.create(null);

        final User user = createMockUser("hans");
        final Subject subject = mock(Subject.class);
        final EntityShareResponse entityShareResponse = entitySharesService.prepareShare(entity, shareRequest, user, subject);
        assertThat(entityShareResponse.activeShares()).satisfies(activeShares -> {
            assertThat(activeShares).isEmpty();
        });

    }
    @DisplayName("Only show shares for visible grantees")
    @Test
    void showShareForVisibleGrantee() {
        final GRN entity = grnRegistry.newGRN(GRNTypes.STREAM, "54e3deadbeefdeadbeefaffe");
        final EntityShareRequest shareRequest = EntityShareRequest.create(null);

        final User user = createMockUser("hans");
        final GRN janeGRN = grnRegistry.newGRN(GRNTypes.USER, "jane");
        when(granteeService.getAvailableGrantees(user)).thenReturn(ImmutableSet.of(EntityShareResponse.AvailableGrantee.create(janeGRN, "user", "jane")));
        final Subject subject = mock(Subject.class);
        final EntityShareResponse entityShareResponse = entitySharesService.prepareShare(entity, shareRequest, user, subject);
        assertThat(entityShareResponse.activeShares()).satisfies(activeShares -> {
            assertThat(activeShares).hasSize(1);
            assertThat(activeShares.iterator().next().grantee()).isEqualTo(janeGRN);
        });
    }

    private User createMockUser(String name) {
        final User user = mock(User.class);
        lenient().when(user.getName()).thenReturn(name);
        lenient().when(user.getId()).thenReturn(name);
        return user;
    }
}
