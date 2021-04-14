/*
 * */
package com.synectiks.process.server.contentpacks.facades.dashboardV1;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.plugins.views.search.elasticsearch.ElasticsearchQueryString;
import com.synectiks.process.common.plugins.views.search.views.Titles;
import com.synectiks.process.common.plugins.views.search.views.WidgetPositionDTO;
import com.synectiks.process.server.contentpacks.model.entities.DashboardEntity;
import com.synectiks.process.server.contentpacks.model.entities.DashboardWidgetEntity;
import com.synectiks.process.server.contentpacks.model.entities.QueryEntity;
import com.synectiks.process.server.contentpacks.model.entities.SearchEntity;
import com.synectiks.process.server.contentpacks.model.entities.SearchTypeEntity;
import com.synectiks.process.server.contentpacks.model.entities.ViewEntity;
import com.synectiks.process.server.contentpacks.model.entities.ViewStateEntity;
import com.synectiks.process.server.contentpacks.model.entities.WidgetEntity;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.InvalidRangeParametersException;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.RelativeRange;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class EntityConverter {

    private DashboardEntity dashboardEntity;
    private Map<String, ValueReference> parameters;
    private DashboardWidgetConverter dashboardWidgetConverter;

    @Inject
    public EntityConverter(DashboardWidgetConverter dashboardWidgetConverter) {
       this.dashboardWidgetConverter = dashboardWidgetConverter;
    }

    public ViewEntity convert(DashboardEntity dashboardEntity, Map<String, ValueReference> parameters) {
        this.parameters = parameters;
        this.dashboardEntity = dashboardEntity;

        final String queryId = UUID.randomUUID().toString();

        final Map<DashboardWidgetEntity, List<WidgetEntity>> widgets = convertWidgets();
        final Map<String, WidgetPositionDTO> widgetPositionMap = DashboardEntity.positionMap(parameters, widgets);
        final  Titles titles = DashboardEntity.widgetTitles(widgets, parameters);

        final Map<String, Set<String>> widgetMapping = new HashMap<>();
        final Set<SearchTypeEntity> searchTypes = new HashSet<>();
        createSearchTypes(widgets, widgetMapping, searchTypes);

        SearchEntity searchEntity;
        try {
            searchEntity = createSearchEntity(queryId, searchTypes);
        } catch (InvalidRangeParametersException e) {
            throw new IllegalArgumentException("The provided entity does not have a valid TimeRange", e);
        }

        final ViewStateEntity viewStateEntity = ViewStateEntity.builder()
                .widgets(widgets.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()))
                .titles(titles)
                .widgetMapping(widgetMapping)
                .widgetPositions(widgetPositionMap)
                .build();

        final Map<String, ViewStateEntity> viewStateEntityMap = ImmutableMap.of(queryId, viewStateEntity);

        return ViewEntity.builder()
                .search(searchEntity)
                .state(viewStateEntityMap)
                .title(dashboardEntity.title())
                .properties(Collections.emptySet())
                .description(dashboardEntity.description())
                .requires(Collections.emptyMap())
                .summary(ValueReference.of("Converted Dashboard"))
                .createdAt(DateTime.now(DateTimeZone.UTC))
                .type(ViewEntity.Type.DASHBOARD)
                .build();
    }

    private Map<DashboardWidgetEntity, List<WidgetEntity>> convertWidgets() {
        final Map<DashboardWidgetEntity, List<WidgetEntity>> widgets = new HashMap<>();
        for (DashboardWidgetEntity widgetEntity : dashboardEntity.widgets()) {
            widgets.put(widgetEntity, dashboardWidgetConverter.convert(widgetEntity, parameters));
        }
        return widgets;
    }

    private void createSearchTypes(Map<DashboardWidgetEntity, List<WidgetEntity>> widgets,
                                   Map<String, Set<String>> widgetMapping,
                                   Set<SearchTypeEntity> searchTypes) {
        for (Map.Entry<DashboardWidgetEntity, List<WidgetEntity>> widgetEntityListEntry: widgets.entrySet()) {
            widgetEntityListEntry.getValue().forEach(widgetEntity -> {
                final List<SearchTypeEntity> currentSearchTypes;
                currentSearchTypes = widgetEntity.createSearchTypeEntity();
                searchTypes.addAll(currentSearchTypes);
                widgetMapping.put(widgetEntity.id(),
                        currentSearchTypes.stream().map(SearchTypeEntity::id).collect(Collectors.toSet()));
            });
        }
    }

    private SearchEntity createSearchEntity(String queryId, Set<SearchTypeEntity> searchTypes)
            throws InvalidRangeParametersException {
        final QueryEntity query = QueryEntity.builder()
                .id(queryId)
                .searchTypes(searchTypes)
                .timerange(RelativeRange.create(300))
                .query(ElasticsearchQueryString.builder().queryString("").build())
                .build();
        return SearchEntity.builder()
                .requires(ImmutableMap.of())
                .parameters(ImmutableSet.of())
                .createdAt(DateTime.now(DateTimeZone.UTC))
                .queries(ImmutableSet.of(query))
                .build();
    }
}
