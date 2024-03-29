/*
 * */
package com.synectiks.process.server.indexer;

import com.google.auto.value.AutoValue;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;

import org.joda.time.Duration;

import javax.inject.Inject;
import java.util.Optional;

public class IndexSetValidator {
    private final static Duration MINIMUM_FIELD_TYPE_REFRESH_INTERVAL = Duration.standardSeconds(1L);
    private final IndexSetRegistry indexSetRegistry;

    @Inject
    public IndexSetValidator(IndexSetRegistry indexSetRegistry) {
        this.indexSetRegistry = indexSetRegistry;
    }

    public Optional<Violation> validate(IndexSetConfig newConfig) {
        // Build an example index name with the new prefix and check if this would be managed by an existing index set
        final String indexName = newConfig.indexPrefix() + MongoIndexSet.SEPARATOR + "0";
        if (indexSetRegistry.isManagedIndex(indexName)) {
            return Optional.of(Violation.create("Index prefix \"" + newConfig.indexPrefix() + "\" would conflict with an existing index set!"));
        }

        // Check if an existing index set has a more generic index prefix.
        // Example: new=compliancemanager_foo existing=compliancemanager => compliancemanager is more generic so this is an error
        // Example: new=compliancemanager        existing=compliancemanager => gray    is more generic so this is an error
        // This avoids problems with wildcard matching like "compliancemanager_*".
        for (final IndexSet indexSet : indexSetRegistry) {
            if (newConfig.indexPrefix().startsWith(indexSet.getIndexPrefix()) || indexSet.getIndexPrefix().startsWith(newConfig.indexPrefix())) {
                return Optional.of(Violation.create("Index prefix \"" + newConfig.indexPrefix() + "\" would conflict with existing index set prefix \"" + indexSet.getIndexPrefix() + "\""));
            }
        }

        // Ensure fieldTypeRefreshInterval is not shorter than a second, as that may impact performance
        if (newConfig.fieldTypeRefreshInterval().isShorterThan(MINIMUM_FIELD_TYPE_REFRESH_INTERVAL)) {
            return Optional.of(Violation.create("Index field_type_refresh_interval \"" + newConfig.fieldTypeRefreshInterval().toString() + "\" is too short. It must be 1 second or longer."));
        }

        return Optional.empty();
    }

    @AutoValue
    public static abstract class Violation {
        public abstract String message();

        public static Violation create(String message) {
            return new AutoValue_IndexSetValidator_Violation(message);
        }
    }
}
