/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.savedsearch.SavedSearch;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.savedsearch.SavedSearchService;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.search.Query;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.search.Search;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.search.SearchService;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.search.SearchType;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view.AggregationWidget;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view.Position;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view.RandomObjectIdProvider;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view.RandomUUIDProvider;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view.Titles;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view.View;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view.ViewService;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view.ViewState;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view.ViewWidget;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view.ViewWidgetPosition;
import com.synectiks.process.server.migrations.Migration;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class V20191203120602_MigrateSavedSearchesToViews extends Migration {
    private static final Logger LOG = LoggerFactory.getLogger(V20191203120602_MigrateSavedSearchesToViews.class);

    private final ClusterConfigService clusterConfigService;
    private final SavedSearchService savedSearchService;
    private final SearchService searchService;
    private final ViewService viewService;
    private final RandomObjectIdProvider randomObjectIdProvider;
    private final RandomUUIDProvider randomUUIDProvider;

    @Inject
    public V20191203120602_MigrateSavedSearchesToViews(ClusterConfigService clusterConfigService,
                                                       SavedSearchService savedSearchService,
                                                       SearchService searchService,
                                                       ViewService viewService,
                                                       RandomObjectIdProvider randomObjectIdProvider,
                                                       RandomUUIDProvider randomUUIDProvider) {
        this.clusterConfigService = clusterConfigService;
        this.savedSearchService = savedSearchService;
        this.searchService = searchService;
        this.viewService = viewService;
        this.randomObjectIdProvider = randomObjectIdProvider;
        this.randomUUIDProvider = randomUUIDProvider;
    }

    @Override
    public ZonedDateTime createdAt() {
        return ZonedDateTime.parse("2019-12-03T12:06:02Z");
    }

    @Override
    public void upgrade() {
        if (hasBeenRunSuccessfully()) {
            LOG.debug("Migration already completed.");
            return;
        }

        final Map<String, String> savedSearchToViewsMap = new HashMap<>();

        final Map<View, Search> newViews = this.savedSearchService.streamAll()
                .map(savedSearch -> {
                    final Map.Entry<View, Search> newView = migrateSavedSearch(savedSearch);
                    savedSearchToViewsMap.put(savedSearch.id(), newView.getKey().id());
                    return newView;
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        newViews.forEach((view, search) -> {
            viewService.save(view);
            searchService.save(search);
        });

        final MigrationCompleted migrationCompleted = MigrationCompleted.create(savedSearchToViewsMap);
        writeMigrationCompleted(migrationCompleted);
    }

    private Map.Entry<View, Search> migrateSavedSearch(SavedSearch savedSearch) {
        final String histogramId = randomUUIDProvider.get();
        final String messageListId = randomUUIDProvider.get();
        final Set<ViewWidget> widgets = ImmutableSet.of(
                AggregationWidget.create(histogramId),
                savedSearch.query().toMessagesWidget(messageListId)
        );
        final Map<String, Set<String>> widgetMapping = new HashMap<>(widgets.size());

        final Set<SearchType> searchTypes = widgets.stream()
                .flatMap(widget -> {
                    final Set<SearchType> widgetSearchTypes = widget.toSearchTypes(randomUUIDProvider);
                    widgetMapping.put(widget.id(), widgetSearchTypes.stream().map(SearchType::id).collect(Collectors.toSet()));
                    return widgetSearchTypes.stream();
                })
                .collect(Collectors.toSet());
        final Query.Builder queryBuilder = Query.builder()
                .id(randomUUIDProvider.get())
                .timerange(savedSearch.query().toTimeRange())
                .query(savedSearch.query().query())
                .searchTypes(searchTypes);
        final Query query = savedSearch.query().streamId().map(queryBuilder::streamId).orElse(queryBuilder).build();
        final Search newSearch = Search.create(randomObjectIdProvider.get(), Collections.singleton(query), savedSearch.creatorUserId(), savedSearch.createdAt());

        final Titles titles = Titles.ofWidgetTitles(ImmutableMap.of(
                histogramId, "Message Count",
                messageListId, "All Messages"
        ));
        final Map<String, ViewWidgetPosition> widgetPositions = ImmutableMap.of(
                histogramId, ViewWidgetPosition.builder()
                        .col(Position.fromInt(1))
                        .row(Position.fromInt(1))
                        .height(Position.fromInt(2))
                        .width(Position.infinity())
                        .build(),
                messageListId, ViewWidgetPosition.builder()
                        .col(Position.fromInt(1))
                        .row(Position.fromInt(3))
                        .height(Position.fromInt(6))
                        .width(Position.infinity())
                        .build()
        );
        final ViewState viewState = ViewState.create(titles, widgets, widgetMapping, widgetPositions);
        final View newView = View.create(
                randomObjectIdProvider.get(),
                "Saved Search: " + savedSearch.title(),
                "This Search was migrated automatically from the \"" + savedSearch.title() + "\" saved search.",
                "",
                newSearch.id(),
                Collections.singletonMap(query.id(), viewState),
                Optional.of(savedSearch.creatorUserId()),
                savedSearch.createdAt()
        );

        return new AbstractMap.SimpleEntry<>(newView, newSearch);
    }

    private boolean hasBeenRunSuccessfully() {
        return clusterConfigService.get(MigrationCompleted.class) != null;
    }

    private void writeMigrationCompleted(MigrationCompleted migrationCompleted) {
        this.clusterConfigService.write(migrationCompleted);
    }

    @AutoValue
    public abstract static class MigrationCompleted {
        static final String FIELD_SAVED_SEARCH_IDS = "saved_search_ids";

        @JsonProperty(FIELD_SAVED_SEARCH_IDS)
        public abstract Map<String, String> savedSearchIds();

        @JsonCreator
        static MigrationCompleted create(@JsonProperty(FIELD_SAVED_SEARCH_IDS) Map<String, String> savedSearchIds) {
            return new AutoValue_V20191203120602_MigrateSavedSearchesToViews_MigrationCompleted(savedSearchIds);
        }
    }
}
