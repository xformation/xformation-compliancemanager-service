/*
 * */
package com.synectiks.process.server.grok;

import com.google.inject.Inject;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.PaginatedDbService;
import com.synectiks.process.server.database.PaginatedList;
import com.synectiks.process.server.search.SearchQuery;

import org.mongojack.DBQuery;
import org.mongojack.DBSort;

public class PaginatedGrokPatternService extends PaginatedDbService<GrokPattern> {
    private static final String COLLECTION_NAME = "grok_patterns";

    @Inject
    public PaginatedGrokPatternService(MongoConnection mongoConnection,
                                  MongoJackObjectMapperProvider mapper)
    {
        super(mongoConnection, mapper, GrokPattern.class, COLLECTION_NAME);
    }

    public long count() {
        return db.count();
    }

    public PaginatedList<GrokPattern> findPaginated(SearchQuery searchQuery, int page, int perPage, String sortField, String order) {
        final DBQuery.Query dbQuery = searchQuery.toDBQuery();
        final DBSort.SortBuilder sortBuilder = getSortBuilder(order, sortField);
        return findPaginatedWithQueryAndSort(dbQuery, sortBuilder, page, perPage);
    }
}
