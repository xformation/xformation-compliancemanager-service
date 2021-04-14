/*
 * */
package com.synectiks.process.server.contentpacks.facades;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.Search;
import com.synectiks.process.common.plugins.views.search.SearchRequirements;
import com.synectiks.process.common.plugins.views.search.db.SearchDbService;
import com.synectiks.process.common.plugins.views.search.filter.OrFilter;
import com.synectiks.process.common.plugins.views.search.filter.QueryStringFilter;
import com.synectiks.process.common.plugins.views.search.filter.StreamFilter;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.PivotSort;
import com.synectiks.process.common.plugins.views.search.views.ViewDTO;
import com.synectiks.process.common.plugins.views.search.views.ViewRequirements;
import com.synectiks.process.common.plugins.views.search.views.ViewService;
import com.synectiks.process.common.plugins.views.search.views.ViewStateDTO;
import com.synectiks.process.common.plugins.views.search.views.WidgetDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.AggregationConfigDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.AutoIntervalDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.BarVisualizationConfigDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.LineVisualizationConfigDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.NumberVisualizationConfigDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.TimeHistogramConfigDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.ValueConfigDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.sort.PivotSortConfig;
import com.synectiks.process.common.plugins.views.search.views.widgets.messagelist.MessageListConfigDTO;
import com.synectiks.process.common.security.entities.EntityOwnershipService;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBInstance;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.contentpacks.facades.dashboardV1.DashboardV1Facade;
import com.synectiks.process.server.contentpacks.facades.dashboardV1.DashboardWidgetConverter;
import com.synectiks.process.server.contentpacks.facades.dashboardV1.EntityConverter;
import com.synectiks.process.server.contentpacks.model.ContentPack;
import com.synectiks.process.server.contentpacks.model.ContentPackV1;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.entities.Entity;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntity;
import com.synectiks.process.server.contentpacks.model.entities.PivotEntity;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.security.PasswordAlgorithmFactory;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;
import com.synectiks.process.server.shared.security.Permissions;
import com.synectiks.process.server.shared.users.UserService;
import com.synectiks.process.server.streams.StreamImpl;
import com.synectiks.process.server.users.UserImpl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DashboardV1FacadeTest {

    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    public static class TestSearchDBService extends SearchDbService {
        protected TestSearchDBService(MongoConnection mongoConnection,
                                      MongoJackObjectMapperProvider mapper) {
            super(mongoConnection, mapper, dto -> new SearchRequirements(Collections.emptySet(), dto));
        }
    }

    public static class TestViewService extends ViewService {
        protected TestViewService(MongoConnection mongoConnection,
                                  MongoJackObjectMapperProvider mapper,
                                  ClusterConfigService clusterConfigService) {
            super(mongoConnection, mapper, clusterConfigService,
                    dto -> new ViewRequirements(Collections.emptySet(), dto), mock(EntityOwnershipService.class));
        }
    }

    private DashboardV1Facade facade;
    private ViewFacadeTest.TestViewService viewService;
    private ViewFacadeTest.TestSearchDBService searchDbService;
    private final String viewId = "5def958063303ae5f68eccae"; /* stored in database */
    private final String newViewId = "5def958063303ae5f68edead";
    private final String newStreamId = "5def958063303ae5f68ebeaf";
    private final String streamId = "5cdab2293d27467fbe9e8a72"; /* stored in database */
    private ViewDTO viewDTO;
    private UserService userService;


    @Before
    public void setUp() throws IOException {
        objectMapper.registerSubtypes(new NamedType(AggregationConfigDTO.class, AggregationConfigDTO.NAME));
        objectMapper.registerSubtypes(new NamedType(MessageListConfigDTO.class, MessageListConfigDTO.NAME));
        objectMapper.registerSubtypes(new NamedType(LineVisualizationConfigDTO.class, LineVisualizationConfigDTO.NAME));
        objectMapper.registerSubtypes(new NamedType(BarVisualizationConfigDTO.class, BarVisualizationConfigDTO.NAME));
        objectMapper.registerSubtypes(new NamedType(NumberVisualizationConfigDTO.class, NumberVisualizationConfigDTO.NAME));
        objectMapper.registerSubtypes(new NamedType(TimeHistogramConfigDTO.class, TimeHistogramConfigDTO.NAME));
        objectMapper.registerSubtypes(new NamedType(ValueConfigDTO.class, ValueConfigDTO.NAME));
        objectMapper.registerSubtypes(new NamedType(PivotSortConfig.class, PivotSortConfig.Type));
        objectMapper.registerSubtypes(new NamedType(PivotEntity.class, PivotEntity.NAME));
        objectMapper.registerSubtypes(new NamedType(PivotSort.class, PivotSort.Type));
        objectMapper.registerSubtypes(new NamedType(OrFilter.class, OrFilter.NAME));
        objectMapper.registerSubtypes(new NamedType(StreamFilter.class, StreamFilter.NAME));
        objectMapper.registerSubtypes(new NamedType(QueryStringFilter.class, QueryStringFilter.NAME));
        objectMapper.registerSubtypes(new NamedType(AutoIntervalDTO.class, AutoIntervalDTO.type));
        searchDbService = new ViewFacadeTest.TestSearchDBService(mongodb.mongoConnection(),
                new MongoJackObjectMapperProvider(objectMapper));
        viewService = new ViewFacadeTest.TestViewService(mongodb.mongoConnection(),
                new MongoJackObjectMapperProvider(objectMapper), null);
        userService = mock(UserService.class);
        final UserImpl fakeUser = new UserImpl(mock(PasswordAlgorithmFactory.class), new Permissions(ImmutableSet.of()), ImmutableMap.of("username", "testuser"));
        when(userService.load("testuser")).thenReturn(fakeUser);
        final DashboardWidgetConverter dashboardWidgetConverter = new DashboardWidgetConverter();
        final EntityConverter entityConverter = new EntityConverter(dashboardWidgetConverter);
        facade = new DashboardV1Facade(objectMapper, searchDbService, entityConverter, viewService, userService);
        final URL resourceUrl = Resources.getResource(DashboardV1Facade.class, "content-pack-dashboard-v1.json");
        final ContentPack contentPack = objectMapper.readValue(resourceUrl, ContentPack.class);
        assertThat(contentPack).isInstanceOf(ContentPackV1.class);
        final ContentPackV1 contentPackV1 = (ContentPackV1) contentPack;
        final Entity entity = contentPackV1.entities().iterator().next();

        final StreamImpl stream = new StreamImpl(Collections.emptyMap());
        final Map<EntityDescriptor, Object> nativeEntities = new HashMap<>(1);
        nativeEntities.put(EntityDescriptor.create("58b3d55a-51ad-4b3e-865c-85776016a151", ModelTypes.STREAM_V1), stream);

        final NativeEntity<ViewDTO> nativeEntity = facade.createNativeEntity(entity,
                ImmutableMap.of(), nativeEntities, "testuser");
        assertThat(nativeEntity).isNotNull();

        viewDTO = nativeEntity.entity();
    }

    @Test
    @MongoDBFixtures("DashboardV1FacadeTest.json")
    public void viewDOTShouldHaveGeneralInformation() {
        assertThat(viewDTO).isNotNull();
        assertThat(viewDTO.title()).matches("ContentPack Dashboard");
        assertThat(viewDTO.description()).matches("A dashboard for content packs");
        assertThat(viewDTO.summary()).matches("Converted Dashboard");
    }

    @Test
    @MongoDBFixtures("DashboardV1FacadeTest.json")
    public void viewDTOShouldHaveACorrectViewState() {
        assertThat(viewDTO.type()).isEqualByComparingTo(ViewDTO.Type.DASHBOARD);
        assertThat(viewDTO.state()).isNotNull();
        assertThat(viewDTO.state().size()).isEqualTo(1);
        ViewStateDTO viewState = viewDTO.state().values().iterator().next();
        assertThat(viewState.widgets().size()).isEqualTo(12);
        final Set<String> widgetIds = viewState.widgets().stream().map(WidgetDTO::id).collect(Collectors.toSet());
        final Set<String> widgetPositionIds = viewState.widgetPositions().keySet();
        assertThat(widgetIds).containsAll(widgetPositionIds);
        widgetIds.forEach(widgetId -> assertThat(viewState.titles().widgetTitle(widgetId)).isPresent());
        widgetIds.forEach(widgetId -> assertThat(viewState.widgetMapping().get(widgetId)).isNotEmpty());
    }

    @Test
    @MongoDBFixtures("DashboardV1FacadeTest.json")
    public void viewDTOShouldHaveACorrectSearch() throws NotFoundException {
        Optional<Search> optionalSearch = searchDbService.get(viewDTO.searchId());
        Search search = optionalSearch.orElseThrow(NotFoundException::new);
        assertThat(search.queries().size()).isEqualTo(1);
        Query query = search.queries().iterator().next();
        assertThat(query.searchTypes().size()).isEqualTo(15);
    }
}
