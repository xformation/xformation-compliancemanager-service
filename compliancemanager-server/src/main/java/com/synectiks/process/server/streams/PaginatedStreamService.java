/*
 * */
package com.synectiks.process.server.streams;

import org.mongojack.DBQuery;
import org.mongojack.DBSort;

import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.PaginatedDbService;
import com.synectiks.process.server.database.PaginatedList;
import com.synectiks.process.server.search.SearchQuery;

import javax.inject.Inject;
import java.util.function.Predicate;

public class PaginatedStreamService extends PaginatedDbService<StreamDTO> {
    private static final String COLLECTION_NAME = "streams";

    @Inject
    public PaginatedStreamService(MongoConnection mongoConnection,
                                  MongoJackObjectMapperProvider mapper)
    {
        super(mongoConnection, mapper, StreamDTO.class, COLLECTION_NAME);
    }

    public long count() {
        return db.count();
    }

    public PaginatedList<StreamDTO> findPaginated(SearchQuery searchQuery, Predicate<StreamDTO> filter, int page,
                                                  int perPage, String sortField, String order) {
        final DBQuery.Query dbQuery = searchQuery.toDBQuery();
        final DBSort.SortBuilder sortBuilder = getSortBuilder(order, sortField);
        return findPaginatedWithQueryFilterAndSort(dbQuery, filter, sortBuilder, page, perPage);
    }
}
