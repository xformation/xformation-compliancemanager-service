/*
 * */
package com.synectiks.process.common.plugins.views.search.elasticsearch;

import com.google.common.collect.Sets;
import com.synectiks.process.server.indexer.fieldtypes.FieldTypeDTO;
import com.synectiks.process.server.indexer.fieldtypes.IndexFieldTypesService;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FieldTypesLookup {
    private final IndexFieldTypesService indexFieldTypesService;

    @Inject
    public FieldTypesLookup(IndexFieldTypesService indexFieldTypesService) {
        this.indexFieldTypesService = indexFieldTypesService;
    }

    private Map<String, Set<String>> get(Set<String> streamIds) {
        return this.indexFieldTypesService.findForStreamIds(streamIds)
                .stream()
                .flatMap(indexFieldTypes -> indexFieldTypes.fields().stream())
                .collect(Collectors.toMap(
                        FieldTypeDTO::fieldName,
                        fieldType -> Collections.singleton(fieldType.physicalType()),
                        Sets::union
                ));
    }

    public Optional<String> getType(Set<String> streamIds, String field) {
        final Map<String, Set<String>> allFieldTypes = this.get(streamIds);
        final Set<String> fieldTypes = allFieldTypes.get(field);
        return fieldTypes == null || fieldTypes.size() > 1 ? Optional.empty() : fieldTypes.stream().findFirst();
    }
}
