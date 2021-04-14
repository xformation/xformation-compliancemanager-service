/*
 * */
package com.synectiks.process.server.rest.resources.system.contentpacks;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.synectiks.process.server.contentpacks.ContentPackInstallationPersistenceService;
import com.synectiks.process.server.contentpacks.ContentPackService;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.contentpacks.constraints.ConstraintChecker;
import com.synectiks.process.server.contentpacks.facades.EntityFacade;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelType;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.EntityExcerpt;
import com.synectiks.process.server.contentpacks.model.entities.EntityV1;
import com.synectiks.process.server.rest.models.system.contentpacks.responses.CatalogIndexResponse;
import com.synectiks.process.server.rest.models.system.contentpacks.responses.CatalogResolveRequest;
import com.synectiks.process.server.rest.models.system.contentpacks.responses.CatalogResolveResponse;
import com.synectiks.process.server.rest.resources.system.contentpacks.CatalogResource;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CatalogResourceTest {
    static {
        GuiceInjectorHolder.createInjector(Collections.emptyList());
    }

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private EntityFacade<Void> mockEntityFacade;

    private ContentPackService contentPackService;
    private CatalogResource catalogResource;

    @Before
    public void setUp() {
        final ContentPackInstallationPersistenceService contentPackInstallationPersistenceService =
                mock(ContentPackInstallationPersistenceService.class);
        final Set<ConstraintChecker> constraintCheckers = Collections.emptySet();
        final Map<ModelType, EntityFacade<?>> entityFacades = Collections.singletonMap(ModelType.of("test", "1"), mockEntityFacade);
        contentPackService = new ContentPackService(contentPackInstallationPersistenceService, constraintCheckers, entityFacades);
        catalogResource = new CatalogResource(contentPackService);
    }

    @Test
    public void showEntityIndex() {
        final ImmutableSet<EntityExcerpt> entityExcerpts = ImmutableSet.of(
                EntityExcerpt.builder()
                        .id(ModelId.of("1234567890"))
                        .type(ModelType.of("test", "1"))
                        .title("Test Entity")
                        .build()
        );
        when(mockEntityFacade.listEntityExcerpts()).thenReturn(entityExcerpts);
        final CatalogIndexResponse catalogIndexResponse = catalogResource.showEntityIndex();

        assertThat(catalogIndexResponse.entities())
                .hasSize(1)
                .containsAll(entityExcerpts);
    }

    @Test
    public void resolveEntities() {
        final EntityDescriptor entityDescriptor = EntityDescriptor.builder()
                .id(ModelId.of("1234567890"))
                .type(ModelType.of("test", "1"))
                .build();
        final MutableGraph<EntityDescriptor> entityDescriptors = GraphBuilder.directed().build();
        entityDescriptors.addNode(entityDescriptor);

        final EntityV1 entity = EntityV1.builder()
                .id(ModelId.of("1234567890"))
                .type(ModelType.of("test", "1"))
                .data(new ObjectNode(JsonNodeFactory.instance).put("test", "1234"))
                .build();
        when(mockEntityFacade.resolveNativeEntity(entityDescriptor)).thenReturn(entityDescriptors);
        when(mockEntityFacade.exportEntity(eq(entityDescriptor), any(EntityDescriptorIds.class))).thenReturn(Optional.of(entity));

        final CatalogResolveRequest request = CatalogResolveRequest.create(entityDescriptors.nodes());

        final CatalogResolveResponse catalogResolveResponse = catalogResource.resolveEntities(request);

        assertThat(catalogResolveResponse.entities()).containsOnly(entity);
    }
}
