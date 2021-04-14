/*
 * */
package com.synectiks.process.server.contentpacks.facades.dashboardV1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.Graph;
import com.synectiks.process.common.plugins.views.search.db.SearchDbService;
import com.synectiks.process.common.plugins.views.search.views.ViewDTO;
import com.synectiks.process.common.plugins.views.search.views.ViewService;
import com.synectiks.process.server.contentpacks.facades.ViewFacade;
import com.synectiks.process.server.contentpacks.model.ModelType;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.entities.DashboardEntity;
import com.synectiks.process.server.contentpacks.model.entities.Entity;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.EntityV1;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntity;
import com.synectiks.process.server.contentpacks.model.entities.ViewEntity;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.shared.users.UserService;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class DashboardV1Facade extends ViewFacade {
    public static final ModelType TYPE_V1 = ModelTypes.DASHBOARD_V1;
    private ObjectMapper objectMapper;
    private EntityConverter entityConverter;

    @Inject
    public DashboardV1Facade(ObjectMapper objectMapper,
                             SearchDbService searchDbService,
                             EntityConverter entityConverter,
                             ViewService viewService,
                             UserService userService) {
        super(objectMapper, searchDbService, viewService, userService);
        this.objectMapper = objectMapper;
        this.entityConverter = entityConverter;
    }

    @Override
    public ViewDTO.Type getDTOType() {
        return ViewDTO.Type.DASHBOARD;
    }

    @Override
    public ModelType getModelType() {
        return ModelTypes.DASHBOARD_V1;
    }

    @Override
    protected Stream<ViewDTO> getNativeViews() {
        /* There are no old dashboards in the system */
        return ImmutableSet.<ViewDTO>of().stream();
    }

    @Override
    public NativeEntity<ViewDTO> createNativeEntity(Entity entity, Map<String, ValueReference> parameters, Map<EntityDescriptor, Object> nativeEntities, String username) {
        ensureV1(entity);
        final User user = Optional.ofNullable(userService.load(username)).orElseThrow(() -> new IllegalStateException("Cannot load user <" + username + "> from db"));
        return decode((EntityV1) entity, parameters, nativeEntities, user);
    }

    @Override
    protected NativeEntity<ViewDTO> decode(EntityV1 entityV1,
                                           Map<String, ValueReference> parameters,
                                           Map<EntityDescriptor, Object> nativeEntities, User user) {
        final EntityV1 convertedEntity = convertEntity(entityV1, parameters);
        return super.decode(convertedEntity, parameters, nativeEntities, user);
    }

    private EntityV1 convertEntity(EntityV1 entityV1,
                                   Map<String, ValueReference> parameters) {
        final DashboardEntity dashboardEntity = objectMapper.convertValue(entityV1.data(), DashboardEntity.class);
        final ViewEntity viewEntity = entityConverter.convert(dashboardEntity, parameters);
        final JsonNode data = objectMapper.convertValue(viewEntity, JsonNode.class);
        return entityV1.toBuilder().data(data).type(ModelTypes.DASHBOARD_V2).build();
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public Graph<Entity> resolveForInstallation(Entity entity,
                                                Map<String, ValueReference> parameters,
                                                Map<EntityDescriptor, Entity> entities) {
        ensureV1(entity);
        return resolveEntityV1((EntityV1) entity, parameters, entities);
    }

    @SuppressWarnings("UnstableApiUsage")
    private Graph<Entity> resolveEntityV1(EntityV1 entity,
                                          Map<String, ValueReference> parameters,
                                          Map<EntityDescriptor, Entity> entities) {

        final DashboardEntity dashboardEntity = objectMapper.convertValue(entity.data(), DashboardEntity.class);
        final ViewEntity viewEntity = entityConverter.convert(dashboardEntity, parameters);
        return resolveViewEntity(entity, viewEntity, entities);
    }
}
