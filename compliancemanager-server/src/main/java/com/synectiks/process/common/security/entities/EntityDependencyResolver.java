/*
 * */
package com.synectiks.process.common.security.entities;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNDescriptorService;
import com.synectiks.process.common.grn.GRNRegistry;
import com.synectiks.process.common.grn.GRNType;
import com.synectiks.process.common.security.DBGrantService;
import com.synectiks.process.server.contentpacks.ContentPackService;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelType;
import com.synectiks.process.server.contentpacks.model.entities.EntityExcerpt;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.firstNonNull;

public class EntityDependencyResolver {
    private final ContentPackService contentPackService;
    private final GRNRegistry grnRegistry;
    private final GRNDescriptorService descriptorService;
    private final DBGrantService grantService;

    @Inject
    public EntityDependencyResolver(ContentPackService contentPackService,
                                    GRNRegistry grnRegistry,
                                    GRNDescriptorService descriptorService,
                                    DBGrantService grantService) {
        this.contentPackService = contentPackService;
        this.grnRegistry = grnRegistry;
        this.descriptorService = descriptorService;
        this.grantService = grantService;
    }

    public ImmutableSet<EntityDescriptor> resolve(GRN entity) {
        // TODO: Replace entity excerpt usage with GRNDescriptors once we implemented GRN descriptors for every entity
        final ImmutableMap<GRN, String> entityExcerpts = contentPackService.listAllEntityExcerpts().stream()
                // TODO: Use the GRNRegistry instead of manually building a GRN. Requires all entity types to be in the registry.
                .collect(ImmutableMap.toImmutableMap(e -> GRNType.create(e.type().name(), e.type().name() + ":").newGRNBuilder().entity(e.id().id()).build(), EntityExcerpt::title));

        final Set<com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor> descriptors = contentPackService.resolveEntities(Collections.singleton(com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor.builder()
                .id(ModelId.of(entity.entity()))
                // TODO: This is a hack! Until we stop using the content-pack dependency resolver, we have to use a different version for dashboards here
                .type(ModelType.of(entity.type(), "dashboard".equals(entity.type()) ? "2" : "1")) // TODO: Any way of NOT hardcoding the version here?
                .build()));

        final ImmutableSet<GRN> dependencies = descriptors.stream()
                .map(descriptor -> grnRegistry.newGRN(descriptor.type().name(), descriptor.id().id()))
                .filter(dependency -> !entity.equals(dependency)) // Don't include the given entity in dependencies
                .collect(ImmutableSet.toImmutableSet());

        final Map<GRN, Set<GRN>> targetOwners = grantService.getOwnersForTargets(dependencies);

        return dependencies.stream()
                .map(dependency -> {
                    String title = entityExcerpts.get(dependency);
                    if (title == null) {
                        title = "unknown dependency: <" + dependency + ">";
                    }
                    return EntityDescriptor.create(
                            dependency,
                            title,
                            getOwners(targetOwners.get(dependency))
                    );
                })
                .collect(ImmutableSet.toImmutableSet());
    }

    private Set<EntityDescriptor.Owner> getOwners(@Nullable Set<GRN> owners) {
        return firstNonNull(owners, Collections.<GRN>emptySet()).stream()
                .map(descriptorService::getDescriptor)
                .map(descriptor -> EntityDescriptor.Owner.create(descriptor.grn(), descriptor.title()))
                .collect(Collectors.toSet());
    }
}
