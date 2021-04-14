/*
 * */
package com.synectiks.process.common.plugins.views.search;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.Search;
import com.synectiks.process.common.plugins.views.search.SearchExecutionGuard;
import com.synectiks.process.common.plugins.views.search.engine.BackendQuery;
import com.synectiks.process.common.plugins.views.search.errors.MissingCapabilitiesException;
import com.synectiks.process.common.plugins.views.search.filter.OrFilter;
import com.synectiks.process.common.plugins.views.search.filter.StreamFilter;
import com.synectiks.process.common.plugins.views.search.views.PluginMetadataSummary;
import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.shared.rest.exceptions.MissingStreamPermissionException;

import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.ForbiddenException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static com.synectiks.process.common.plugins.views.search.TestData.requirementsMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

public class SearchExecutionGuardTest {
    private SearchExecutionGuard sut;
    private Map<String, PluginMetaData> providedCapabilities;

    @Before
    public void setUp() throws Exception {
        GuiceInjectorHolder.createInjector(Collections.emptyList());

        providedCapabilities = new HashMap<>();
        providedCapabilities.put("my only capability", mock(PluginMetaData.class));

        sut = new SearchExecutionGuard(providedCapabilities);
    }

    @Test
    public void failsForNonPermittedStreams() {
        final Search search = searchWithStreamIds("ok", "not-ok");

        assertThatExceptionOfType(MissingStreamPermissionException.class)
                .isThrownBy(() -> sut.check(search, id -> id.equals("ok")))
                .satisfies(ex -> assertThat(ex.streamsWithMissingPermissions()).contains("not-ok"));
    }

    @Test
    public void succeedsIfAllStreamsArePermitted() {
        final Search search = searchWithStreamIds("ok", "ok-too", "this is fine...");

        assertSucceeds(search, id -> true);
    }

    @Test
    public void allowsSearchesWithNoStreams() {
        final Search search = searchWithStreamIds();

        assertSucceeds(search, id -> true);
    }

    @Test
    public void failsForMissingCapabilities() {
        final Search search = searchWithCapabilityRequirements("awesomeness");

        assertThatExceptionOfType(MissingCapabilitiesException.class)
                .isThrownBy(() -> sut.check(search, id -> true))
                .satisfies(ex -> assertThat(ex.getMissingRequirements()).containsOnlyKeys("awesomeness"));
    }

    @Test
    public void succeedsIfCapabilityRequirementsFulfilled() {
        final String onlyRequirement = new ArrayList<>(providedCapabilities.keySet()).get(0);
        final Search search = searchWithCapabilityRequirements(onlyRequirement);

        assertSucceeds(search, id -> true);
    }

    private void assertSucceeds(Search search, Predicate<String> isStreamIdPermitted) {
        assertThatCode(() -> sut.check(search, isStreamIdPermitted)).doesNotThrowAnyException();
    }

    private Search searchWithCapabilityRequirements(String... requirementNames) {
        final Search search = searchWithStreamIds("streamId");

        final Map<String, PluginMetadataSummary> requirements = requirementsMap(requirementNames);

        return search.toBuilder().requires(requirements).build();
    }

    private Search searchWithStreamIds(String... streamIds) {
        final StreamFilter[] filters = Arrays.stream(streamIds).map(StreamFilter::ofId).toArray(StreamFilter[]::new);

        final Query query = Query.builder()
                .id("")
                .timerange(mock(TimeRange.class))
                .query(new BackendQuery.Fallback())
                .filter(OrFilter.or(filters))
                .build();
        return Search.Builder.create().id("searchId").queries(ImmutableSet.of(query)).build();
    }
}
