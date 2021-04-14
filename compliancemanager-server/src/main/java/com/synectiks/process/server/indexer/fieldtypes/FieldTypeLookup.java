/*
 * */
package com.synectiks.process.server.indexer.fieldtypes;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Interface for field type lookups.
 */
public interface FieldTypeLookup {
    /**
     * Returns the {@link FieldTypes} object for the given message field name.
     *
     * @param fieldName name of the field to get the type for
     * @return field type object
     */
    Optional<FieldTypes> get(String fieldName);

    /**
     * Returns a map of field names to {@link FieldTypes} objects.
     *
     * @param fieldNames a collection of field names to get the types for
     * @return map of field names to field type objects
     */
    Map<String, FieldTypes> get(Collection<String> fieldNames);

    /**
     * Returns a map of field names to the corresponding field types.
     *
     * @param fieldNames a collection of field names to get the types for
     * @param indexNames a collection of index names to filter the results
     * @return map of field names to field type objects
     */
    Map<String, FieldTypes> get(Collection<String> fieldNames, Collection<String> indexNames);
}
