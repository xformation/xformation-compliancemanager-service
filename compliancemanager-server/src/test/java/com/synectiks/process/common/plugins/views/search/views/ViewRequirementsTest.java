/*
 * */
package com.synectiks.process.common.plugins.views.search.views;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.synectiks.process.common.plugins.views.search.views.EnterpriseMetadataSummary;
import com.synectiks.process.common.plugins.views.search.views.PluginMetadataSummary;
import com.synectiks.process.common.plugins.views.search.views.ViewDTO;
import com.synectiks.process.common.plugins.views.search.views.ViewRequirements;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ViewRequirementsTest {
    private final ViewDTO view = ViewDTO.builder()
            .title("Sample View")
            .state(Collections.emptyMap())
            .searchId("searchId")
            .build();

    private final PluginMetadataSummary plugin = PluginMetadataSummary.create(
            "com.synectiks.process.common.plugins.aioverlord",
            "compliancemanager AI Overlord",
            "garybot@compliancemanager.org",
            new URI("https://www.compliancemanager.org/ai"),
            "3.0.0",
            "compliancemanager becomes self-aware at 2:14AM."
    );

    public ViewRequirementsTest() throws URISyntaxException {
    }

    @Test
    public void isEmptyIfChecksAreEmpty() {
        final Map<String, PluginMetadataSummary> result = new ViewRequirements(Collections.emptySet(), view);

        assertThat(result).isEmpty();
    }

    @Test
    public void returnsResultFromSingleCheck() {
        final Map<String, PluginMetadataSummary> result = new ViewRequirements(
                Collections.singleton(view -> Collections.singletonMap("aioverlord", plugin)),
                view
        );

        assertThat(result).containsExactly(
                Maps.immutableEntry("aioverlord", plugin)
        );
    }

    @Test
    public void mergesResultsFromMultipleChecks() {
        final Map<String, PluginMetadataSummary> result = new ViewRequirements(
                ImmutableSet.of(
                        view -> Collections.singletonMap("aioverlord", plugin),
                        view -> Collections.emptyMap(),
                        view -> Collections.singletonMap("parameters", new EnterpriseMetadataSummary())
                ),
                view
        );

        assertThat(result).containsOnly(
                Maps.immutableEntry("aioverlord", plugin),
                Maps.immutableEntry("parameters", new EnterpriseMetadataSummary())
        );
    }

    @Test
    public void mergesResultsFromMultipleChecksWithConflictingKeys() {
        final Map<String, PluginMetadataSummary> result = new ViewRequirements(
                ImmutableSet.of(
                        view -> Collections.singletonMap("parameters", plugin),
                        view -> Collections.emptyMap(),
                        view -> Collections.singletonMap("parameters", new EnterpriseMetadataSummary())
                ),
                view
        );

        assertThat(result).containsOnly(
                Maps.immutableEntry("parameters", plugin)
        );
    }

    @Test
    public void mergesMultipleResultsFromMultipleChecksWithConflictingKeys() {
        final Map<String, PluginMetadataSummary> result = new ViewRequirements(
                ImmutableSet.of(
                        view -> ImmutableMap.of(
                                "parameters", plugin,
                                "aioverlord", plugin
                        ),
                        view -> Collections.emptyMap(),
                        view -> ImmutableMap.of(
                                "parameters", new EnterpriseMetadataSummary(),
                                "aioverlord", new EnterpriseMetadataSummary()
                        )
                ),
                view
        );

        assertThat(result).contains(
                Maps.immutableEntry("aioverlord", plugin),
                Maps.immutableEntry("parameters", plugin)
        );
    }
}
