/*
 * */
package com.synectiks.process.server.indexer;

import com.google.common.collect.ComparisonChain;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.indexer.indices.TooManyAliasesException;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public interface IndexSet extends Comparable<IndexSet> {
    /**
     * Returns an array with all managed indices in this index set.
     * <p>
     * Example: {@code ["compliancemanager_0", "compliancemanager_1", "compliancemanager_2"]}
     *
     * @return array of index names
     */
    String[] getManagedIndices();

    /**
     * Returns the write index alias name for this index set.
     * <p>
     * The write index alias always points to the newest index.
     * <p>
     * Example: {@code "compliancemanager_deflector"}
     *
     * @return the write index alias name
     */
    String getWriteIndexAlias();

    /**
     * Returns the index wildcard for this index set.
     * <p>
     * This can be used in Elasticsearch queries to match all managed indices in this index set.
     * <p>
     * Example: {@code "compliancemanager_*"}
     *
     * @return the index wildcard
     */
    String getIndexWildcard();

    /**
     * Returns the newest index.
     * <p>
     * Example: {@code "compliancemanager_42"}
     *
     * @return the newest index
     * @throws NoTargetIndexException if there are no indices in this index set yet
     */
    String getNewestIndex() throws NoTargetIndexException;

    /**
     * Returns the active write index.
     * <p>
     * Incoming messages for this index set will be written into this index.
     * <p>
     * Example: {@code "compliancemanager_42"}
     *
     * @return the active write index
     * @throws TooManyAliasesException if the write index alias points to more than one index
     */
    @Nullable
    String getActiveWriteIndex() throws TooManyAliasesException;

    /**
     * Returns a map where the key is an index name and the value a set of aliases for this index.
     * <p>
     * Only the active write index should have an alias, the other values should be empty.
     * <p>
     * Example: {@code {compliancemanager_0=[], compliancemanager_1=[], compliancemanager_2=[compliancemanager_deflector}}
     *
     * @return map of index names to index aliases
     */
    Map<String, Set<String>> getAllIndexAliases();

    /**
     * Returns the index prefix for this index set.
     * <p>
     * Example: {@code "compliancemanager"}
     *
     * @return index prefix for this index set
     */
    String getIndexPrefix();

    /**
     * Checks if the write index alias exists.
     *
     * @return true if the write index alias exists, false if not
     */
    boolean isUp();

    /**
     * Checks if the given index name is equals to the write index alias.
     *
     * @param index index name to check
     * @return true if given index name is the write index alias, false if not
     */
    boolean isWriteIndexAlias(String index);

    /**
     * Checks if the given index name is part of this index set.
     *
     * @param index index name to check
     * @return true if part of index set, false if not
     */
    boolean isManagedIndex(String index);

    /**
     * Prepares this index set to receive new messages.
     */
    void setUp();

    /**
     * Creates a new index and points the write index alias to it.
     */
    void cycle();

    /**
     * This ensures that the write index alias only points to the newest index.
     * <p>
     * Can be used to fix the aliases in this index set when a {@link TooManyAliasesException} has been thrown.
     *
     * @param indices list of indices where the index alias points to
     */
    void cleanupAliases(Set<String> indices);

    /**
     * Changes the write index alias from the old index to the new one.
     *
     * @param newIndexName index to add the write index alias to
     * @param oldIndexName index to remove the write index alias from
     */
    void pointTo(String newIndexName, String oldIndexName);

    /**
     * Extracts the index number from an index name.
     * <p>
     * Example: {@code "compliancemanager_42" => 42}
     *
     * @param index index name
     * @return a filled {@link Optional} with the extracted index number, an empty one if the number couldn't be parsed
     */
    Optional<Integer> extractIndexNumber(String index);

    /**
     * The configuration for this index set.
     *
     * @return index set configuration object
     */
    IndexSetConfig getConfig();

    class IndexNameComparator implements Comparator<String> {
        private final IndexSet indexSet;

        IndexNameComparator(IndexSet indexSet) {
            this.indexSet = requireNonNull(indexSet);
        }

        @Override
        public int compare(String o1, String o2) {
            final int indexNumber1 = indexSet.extractIndexNumber(o1).orElse(-1);
            final int indexNumber2 = indexSet.extractIndexNumber(o2).orElse(-1);
            return ComparisonChain.start()
                    .compare(indexNumber1, indexNumber2)
                    .result();
        }
    }
}
