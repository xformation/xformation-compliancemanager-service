/*
 * */
package com.synectiks.process.common.plugins.sidecar.services;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.mongojack.DBQuery;
import org.mongojack.DBSort;

import com.synectiks.process.common.plugins.sidecar.rest.models.CollectorUpload;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.PaginatedDbService;
import com.synectiks.process.server.database.PaginatedList;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImportService extends PaginatedDbService<CollectorUpload> {
    private static final String COLLECTION_NAME = "collector_uploads";

    @Inject
    public ImportService(MongoConnection mongoConnection,
                         MongoJackObjectMapperProvider mapper){
        super(mongoConnection, mapper, CollectorUpload.class, COLLECTION_NAME);
    }

    public PaginatedList<CollectorUpload> findPaginated(int page, int perPage, String sortField, String order) {
        final DBSort.SortBuilder sortBuilder = getSortBuilder(order, sortField);
        return findPaginatedWithQueryAndSort(DBQuery.empty(), sortBuilder, page, perPage);
    }


    public List<CollectorUpload> all() {
        try (final Stream<CollectorUpload> collectorUploadStream = streamAll()) {
            return collectorUploadStream.collect(Collectors.toList());
        }
    }

    public long count() {
        return db.count();
    }

    public int destroyExpired(Period period) {
        final DateTime threshold = DateTime.now(DateTimeZone.UTC).minus(period);
        int count;

        try (final Stream<CollectorUpload> uploadStream = streamAll()) {
            count = uploadStream
                    .mapToInt(upload -> {
                        if (upload.created().isBefore(threshold)) {
                            return delete(upload.id());
                        }
                        return 0;
                    })
                    .sum();
        }

        return count;
    }
}
