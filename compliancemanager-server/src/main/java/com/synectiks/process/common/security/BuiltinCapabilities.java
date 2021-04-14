/*
 * */
package com.synectiks.process.common.security;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.plugins.views.search.rest.ViewsRestPermissions;
import com.synectiks.process.server.shared.security.RestPermissions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class BuiltinCapabilities {
    private static ImmutableMap<Capability, CapabilityDescriptor> CAPABILITIES;

    @Inject
    public BuiltinCapabilities() {
        CAPABILITIES = ImmutableMap.<Capability, CapabilityDescriptor>builder()
                .put(Capability.VIEW, CapabilityDescriptor.create(
                        Capability.VIEW,
                        "Viewer",
                        ImmutableSet.of(
                                RestPermissions.STREAMS_READ,
                                RestPermissions.DASHBOARDS_READ,
                                ViewsRestPermissions.VIEW_READ,
                                RestPermissions.EVENT_DEFINITIONS_READ,
                                RestPermissions.EVENT_NOTIFICATIONS_READ
                        )
                ))
                .put(Capability.MANAGE, CapabilityDescriptor.create(
                        Capability.MANAGE,
                        "Manager",
                        ImmutableSet.of(
                                RestPermissions.STREAMS_READ,
                                RestPermissions.STREAMS_EDIT,
                                RestPermissions.STREAMS_CHANGESTATE,
                                RestPermissions.DASHBOARDS_READ,
                                RestPermissions.DASHBOARDS_EDIT,
                                ViewsRestPermissions.VIEW_READ,
                                ViewsRestPermissions.VIEW_EDIT,
                                RestPermissions.EVENT_DEFINITIONS_READ,
                                RestPermissions.EVENT_DEFINITIONS_EDIT,
                                RestPermissions.EVENT_NOTIFICATIONS_READ,
                                RestPermissions.EVENT_NOTIFICATIONS_EDIT
                        )
                ))
                .put(Capability.OWN, CapabilityDescriptor.create(
                        Capability.OWN,
                        "Owner",
                        ImmutableSet.of(
                                RestPermissions.ENTITY_OWN,
                                RestPermissions.STREAMS_READ,
                                RestPermissions.STREAMS_EDIT,
                                RestPermissions.STREAMS_CHANGESTATE,
                                RestPermissions.DASHBOARDS_READ,
                                RestPermissions.DASHBOARDS_EDIT,
                                ViewsRestPermissions.VIEW_READ,
                                ViewsRestPermissions.VIEW_EDIT,
                                ViewsRestPermissions.VIEW_DELETE,
                                RestPermissions.EVENT_DEFINITIONS_READ,
                                RestPermissions.EVENT_DEFINITIONS_EDIT,
                                RestPermissions.EVENT_DEFINITIONS_DELETE,
                                RestPermissions.EVENT_NOTIFICATIONS_READ,
                                RestPermissions.EVENT_NOTIFICATIONS_EDIT,
                                RestPermissions.EVENT_NOTIFICATIONS_DELETE
                        )
                ))
                .build();
    }

    public static ImmutableSet<CapabilityDescriptor> allSharingCapabilities() {
        return ImmutableSet.of(
                CAPABILITIES.get(Capability.VIEW),
                CAPABILITIES.get(Capability.MANAGE),
                CAPABILITIES.get(Capability.OWN)
        );
    }

    public Optional<CapabilityDescriptor> get(Capability capability) {
        return Optional.ofNullable(CAPABILITIES.get(capability));
    }
}
