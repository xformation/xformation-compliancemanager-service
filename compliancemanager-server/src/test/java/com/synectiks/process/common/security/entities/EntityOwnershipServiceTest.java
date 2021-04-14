/*
 * */
package com.synectiks.process.common.security.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNRegistry;
import com.synectiks.process.common.grn.GRNType;
import com.synectiks.process.common.grn.GRNTypes;
import com.synectiks.process.common.security.Capability;
import com.synectiks.process.common.security.DBGrantService;
import com.synectiks.process.common.security.GrantDTO;
import com.synectiks.process.common.security.entities.EntityOwnershipService;
import com.synectiks.process.server.plugin.database.users.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EntityOwnershipServiceTest {

    private EntityOwnershipService entityOwnershipService;
    private DBGrantService dbGrantService;
    private GRNRegistry grnRegistry = GRNRegistry.createWithBuiltinTypes();

    @BeforeEach
    void setUp() {
        this.dbGrantService = mock(DBGrantService.class);
        this.entityOwnershipService = new EntityOwnershipService(dbGrantService, grnRegistry);
    }

    @Test
    void registerNewEventDefinition() {
        final User mockUser = mock(User.class);
        when(mockUser.getName()).thenReturn("mockuser");
        when(mockUser.getId()).thenReturn("mockuser");

        entityOwnershipService.registerNewEventDefinition("1234", mockUser);

        ArgumentCaptor<GrantDTO> grant = ArgumentCaptor.forClass(GrantDTO.class);
        ArgumentCaptor<User> user = ArgumentCaptor.forClass(User.class);
        verify(dbGrantService).create(grant.capture(), user.capture());

        assertThat(grant.getValue()).satisfies(g -> {
            assertThat(g.capability()).isEqualTo(Capability.OWN);
            assertThat(g.target().type()).isEqualTo(GRNTypes.EVENT_DEFINITION.type());
            assertThat(g.target().entity()).isEqualTo("1234");
            assertThat(g.grantee().type()).isEqualTo(GRNTypes.USER.type());
            assertThat(g.grantee().entity()).isEqualTo("mockuser");
        });
    }

    @Test
    void unregisterDashboard() {
        entityOwnershipService.unregisterDashboard("1234");
        assertGrantRemoval(GRNTypes.DASHBOARD, "1234");
    }

    @Test
    void unregisterSearch() {
        entityOwnershipService.unregisterSearch("1234");
        assertGrantRemoval(GRNTypes.SEARCH, "1234");
    }

    @Test
    void unregisterEventDefinition() {
        entityOwnershipService.unregisterEventDefinition("1234");
        assertGrantRemoval(GRNTypes.EVENT_DEFINITION, "1234");
    }

    @Test
    void unregisterEventNotification() {
        entityOwnershipService.unregisterEventNotification("1234");
        assertGrantRemoval(GRNTypes.EVENT_NOTIFICATION, "1234");
    }

    @Test
    void unregisterStream() {
        entityOwnershipService.unregisterStream("123");
        assertGrantRemoval(GRNTypes.STREAM, "123");
    }

    private void assertGrantRemoval(GRNType grnType, String entity) {
        ArgumentCaptor<GRN> argCaptor = ArgumentCaptor.forClass(GRN.class);
        verify(dbGrantService).deleteForTarget(argCaptor.capture());

        assertThat(argCaptor.getValue()).satisfies(grn -> {
            assertThat(grn.grnType()).isEqualTo(grnType);
            assertThat(grn.entity()).isEqualTo(entity);
        });
    }
}
