/*
 * */
package com.synectiks.process.common.security.entities;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNDescriptor;
import com.synectiks.process.common.grn.GRNDescriptorService;
import com.synectiks.process.common.grn.GRNRegistry;
import com.synectiks.process.common.security.DBGrantService;
import com.synectiks.process.common.security.entities.EntityDependencyResolver;
import com.synectiks.process.common.testing.GRNExtension;
import com.synectiks.process.common.testing.mongodb.MongoDBExtension;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBTestService;
import com.synectiks.process.common.testing.mongodb.MongoJackExtension;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.contentpacks.ContentPackService;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.EntityExcerpt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MongoDBExtension.class)
@ExtendWith(MongoJackExtension.class)
@ExtendWith(GRNExtension.class)
@ExtendWith(MockitoExtension.class)
@MongoDBFixtures("EntityDependencyResolverTest.json")
class EntityDependencyResolverTest {

    private EntityDependencyResolver entityDependencyResolver;
    private GRNRegistry grnRegistry;
    private ContentPackService contentPackService;
    private GRNDescriptorService grnDescriptorService;

    @BeforeEach
    void setUp(@Mock ContentPackService contentPackService,
               GRNRegistry grnRegistry,
               @Mock GRNDescriptorService grnDescriptorService,
               MongoDBTestService mongodb,
               MongoJackObjectMapperProvider objectMapperProvider) {

        this.grnRegistry = grnRegistry;
        DBGrantService dbGrantService = new DBGrantService(mongodb.mongoConnection(), objectMapperProvider, this.grnRegistry);
        this.contentPackService = contentPackService;
        this.grnDescriptorService = grnDescriptorService;
        entityDependencyResolver = new EntityDependencyResolver(contentPackService, grnRegistry, grnDescriptorService, dbGrantService);
    }

    @Test
    @DisplayName("Try a regular depency resolve")
    void resolve() {
        final String TEST_TITLE = "Test Stream Title";
        final EntityExcerpt streamExcerpt = EntityExcerpt.builder()
                .type(ModelTypes.STREAM_V1)
                .id(ModelId.of("54e3deadbeefdeadbeefaffe"))
                .title(TEST_TITLE).build();
        when(contentPackService.listAllEntityExcerpts()).thenReturn(ImmutableSet.of(streamExcerpt));

        final EntityDescriptor streamDescriptor = EntityDescriptor.builder().type(ModelTypes.STREAM_V1).id(ModelId.of("54e3deadbeefdeadbeefaffe")).build();
        when(contentPackService.resolveEntities(any())).thenReturn(ImmutableSet.of(streamDescriptor));

        when(grnDescriptorService.getDescriptor(any(GRN.class))).thenAnswer(a -> {
            GRN grnArg = a.getArgument(0);
            return GRNDescriptor.builder().grn(grnArg).title("dummy").build();
        });
        final GRN dashboard = grnRegistry.newGRN("dashboard", "33e3deadbeefdeadbeefaffe");

        final ImmutableSet<com.synectiks.process.common.security.entities.EntityDescriptor> missingDependencies = entityDependencyResolver.resolve(dashboard);
        assertThat(missingDependencies).hasSize(1);
        assertThat(missingDependencies.asList().get(0)).satisfies(descriptor -> {
            assertThat(descriptor.id().toString()).isEqualTo("grn::::stream:54e3deadbeefdeadbeefaffe");
            assertThat(descriptor.title()).isEqualTo(TEST_TITLE);

            assertThat(descriptor.owners()).hasSize(1);
            assertThat(descriptor.owners().asList().get(0).id().toString()).isEqualTo("grn::::user:jane");
        });
    }

    @Test
    @DisplayName("Try resolve with a broken dependency")
    void resolveWithInclompleteDependency() {

        when(contentPackService.listAllEntityExcerpts()).thenReturn(ImmutableSet.of());
        final EntityDescriptor streamDescriptor = EntityDescriptor.builder().type(ModelTypes.STREAM_V1).id(ModelId.of("54e3deadbeefdeadbeefaffe")).build();
        when(contentPackService.resolveEntities(any())).thenReturn(ImmutableSet.of(streamDescriptor));

        when(grnDescriptorService.getDescriptor(any(GRN.class))).thenAnswer(a -> {
            GRN grnArg = a.getArgument(0);
            return GRNDescriptor.builder().grn(grnArg).title("dummy").build();
        });
        final GRN dashboard = grnRegistry.newGRN("dashboard", "33e3deadbeefdeadbeefaffe");

        final ImmutableSet<com.synectiks.process.common.security.entities.EntityDescriptor> missingDependencies = entityDependencyResolver.resolve(dashboard);
        assertThat(missingDependencies).hasSize(1);
        assertThat(missingDependencies.asList().get(0)).satisfies(descriptor -> {
            assertThat(descriptor.id().toString()).isEqualTo("grn::::stream:54e3deadbeefdeadbeefaffe");
            assertThat(descriptor.title()).isEqualTo("unknown dependency: <grn::::stream:54e3deadbeefdeadbeefaffe>");
        });
    }
}
