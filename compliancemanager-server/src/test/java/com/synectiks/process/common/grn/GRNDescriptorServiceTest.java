/*
 * */
package com.synectiks.process.common.grn;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNDescriptor;
import com.synectiks.process.common.grn.GRNDescriptorProvider;
import com.synectiks.process.common.grn.GRNDescriptorService;
import com.synectiks.process.common.grn.GRNRegistry;
import com.synectiks.process.common.grn.GRNType;
import com.synectiks.process.common.grn.GRNTypes;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GRNDescriptorServiceTest {
    private final GRNRegistry grnRegistry = GRNRegistry.createWithBuiltinTypes();
    private final GRN user = grnRegistry.newGRN("user", "jane");
    private final GRN dashboard = grnRegistry.newGRN("dashboard", "abc123");

    @Test
    void getDescriptor() {
        final ImmutableMap<GRNType, GRNDescriptorProvider> providers =  ImmutableMap.of(
                user.grnType(), grn -> GRNDescriptor.create(grn, "Jane Doe")
        );
        final GRNDescriptorService service = new GRNDescriptorService(providers);

        assertThat(service.getDescriptor(user)).satisfies(descriptor -> {
            assertThat(descriptor.grn()).isEqualTo(user);
            assertThat(descriptor.title()).isEqualTo("Jane Doe");
        });
    }

    @Test
    void getDescriptors() {
        final ImmutableMap<GRNType, GRNDescriptorProvider> providers =  ImmutableMap.of(
                user.grnType(), grn -> GRNDescriptor.create(grn, "Jane Doe"),
                dashboard.grnType(), grn -> GRNDescriptor.create(grn, "A Dashboard")
        );
        final GRNDescriptorService service = new GRNDescriptorService(providers);

        final Set<GRNDescriptor> descriptors = service.getDescriptors(ImmutableSet.of(user, dashboard));

        assertThat(descriptors).containsExactlyInAnyOrder(
                GRNDescriptor.create(GRNTypes.USER.toGRN("jane"), "Jane Doe"),
                GRNDescriptor.create(GRNTypes.DASHBOARD.toGRN("abc123"), "A Dashboard")
        );
    }

    @Test
    void getDescriptorWithoutProvider() {
        final GRNDescriptorService service = new GRNDescriptorService(ImmutableMap.of());

        assertThatThrownBy(() -> service.getDescriptor(user))
                .hasMessageContaining(user.type())
                .isInstanceOf(IllegalStateException.class);
    }
}
