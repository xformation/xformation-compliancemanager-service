/*
 * */
package com.synectiks.process.common.plugins.views.search;

import com.google.common.base.Joiner;
import com.synectiks.process.common.plugins.views.search.errors.MissingCapabilitiesException;
import com.synectiks.process.common.plugins.views.search.views.PluginMetadataSummary;
import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.shared.rest.exceptions.MissingStreamPermissionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SearchExecutionGuard {

    private static final Logger LOG = LoggerFactory.getLogger(SearchExecutionGuard.class);

    private final Map<String, PluginMetaData> providedCapabilities;

    @Inject
    public SearchExecutionGuard(Map<String, PluginMetaData> providedCapabilities) {
        this.providedCapabilities = providedCapabilities;
    }

    public void check(Search search, Predicate<String> hasReadPermissionForStream) {
        checkUserIsPermittedToSeeStreams(search.usedStreamIds(), hasReadPermissionForStream);

        checkMissingRequirements(search);
    }

    public void checkUserIsPermittedToSeeStreams(Set<String> streamIds, Predicate<String> hasReadPermissionForStream) {
        final Predicate<String> isForbidden = hasReadPermissionForStream.negate();
        final Set<String> forbiddenStreams = streamIds.stream().filter(isForbidden).collect(Collectors.toSet());

        if (!forbiddenStreams.isEmpty()) {
            throwExceptionMentioningStreamIds(forbiddenStreams);
        }
    }

    private void throwExceptionMentioningStreamIds(Set<String> forbiddenStreams) {
        LOG.warn("Not executing search, it is referencing inaccessible streams: [" + Joiner.on(',').join(forbiddenStreams) + "]");
        throw new MissingStreamPermissionException("The search is referencing at least one stream you are not permitted to see.",
                forbiddenStreams);
    }

    private void checkMissingRequirements(Search search) {
        final Map<String, PluginMetadataSummary> missingRequirements = missingRequirementsForEach(search);
        if (!missingRequirements.isEmpty()) {
            throw new MissingCapabilitiesException(missingRequirements);
        }
    }

    private Map<String, PluginMetadataSummary> missingRequirementsForEach(Search search) {
        return search.requires().entrySet().stream()
                .filter(entry -> !this.providedCapabilities.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
