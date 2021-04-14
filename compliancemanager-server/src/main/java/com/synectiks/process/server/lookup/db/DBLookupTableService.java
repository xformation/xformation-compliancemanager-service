/*
 * */
package com.synectiks.process.server.lookup.db;

import com.google.common.collect.ImmutableList;
import com.mongodb.BasicDBObject;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.PaginatedList;
import com.synectiks.process.server.events.ClusterEventBus;
import com.synectiks.process.server.lookup.dto.LookupTableDto;
import com.synectiks.process.server.lookup.events.LookupTablesDeleted;
import com.synectiks.process.server.lookup.events.LookupTablesUpdated;

import org.bson.types.ObjectId;
import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBSort;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DBLookupTableService {
    private final JacksonDBCollection<LookupTableDto, ObjectId> db;
    private final ClusterEventBus clusterEventBus;

    @Inject
    public DBLookupTableService(MongoConnection mongoConnection,
                                MongoJackObjectMapperProvider mapper,
                                ClusterEventBus clusterEventBus) {
        this.db = JacksonDBCollection.wrap(mongoConnection.getDatabase().getCollection("lut_tables"),
                LookupTableDto.class,
                ObjectId.class,
                mapper.get());
        this.clusterEventBus = clusterEventBus;

        db.createIndex(new BasicDBObject("name", 1), new BasicDBObject("unique", true));
    }

    public Optional<LookupTableDto> get(String idOrName) {
        try {
            return Optional.ofNullable(db.findOneById(new ObjectId(idOrName)));
        } catch (IllegalArgumentException e) {
            // not an ObjectId, try again with name
            return Optional.ofNullable(db.findOne(DBQuery.is("name", idOrName)));

        }
    }

    public LookupTableDto save(LookupTableDto table) {
        WriteResult<LookupTableDto, ObjectId> save = db.save(table);
        final LookupTableDto savedLookupTable = save.getSavedObject();

        clusterEventBus.post(LookupTablesUpdated.create(savedLookupTable));

        return savedLookupTable;
    }

    public Collection<LookupTableDto> findAll() {
        return asImmutableList(db.find());
    }

    public Collection<LookupTableDto> findByNames(Collection<String> names) {
        final DBQuery.Query query = DBQuery.in("name", names);
        final DBCursor<LookupTableDto> dbCursor = db.find(query);
        return asImmutableList(dbCursor);
    }

    public PaginatedList<LookupTableDto> findPaginated(DBQuery.Query query, DBSort.SortBuilder sort, int page, int perPage) {
        try (DBCursor<LookupTableDto> cursor = db.find(query)
                .sort(sort)
                .limit(perPage)
                .skip(perPage * Math.max(0, page - 1))) {

            return new PaginatedList<>(asImmutableList(cursor), cursor.count(), page, perPage);
        }
    }

    public Collection<LookupTableDto> findByCacheIds(Collection<String> cacheIds) {
        final DBQuery.Query query = DBQuery.in("cache", cacheIds.stream().map(ObjectId::new).collect(Collectors.toList()));
        try (DBCursor<LookupTableDto> cursor = db.find(query)) {
            return asImmutableList(cursor);
        }
    }

    public Collection<LookupTableDto> findByDataAdapterIds(Collection<String> dataAdapterIds) {
        final DBQuery.Query query = DBQuery.in("data_adapter", dataAdapterIds.stream().map(ObjectId::new).collect(Collectors.toList()));
        try (DBCursor<LookupTableDto> cursor = db.find(query)) {
            return asImmutableList(cursor);
        }
    }

    private ImmutableList<LookupTableDto> asImmutableList(Iterator<? extends LookupTableDto> cursor) {
        return ImmutableList.copyOf(cursor);
    }

    public void delete(String idOrName) {
        final Optional<LookupTableDto> lookupTableDto = get(idOrName);
        lookupTableDto
                .map(LookupTableDto::id)
                .map(ObjectId::new)
                .ifPresent(db::removeById);
        lookupTableDto.ifPresent(lookupTable -> clusterEventBus.post(LookupTablesDeleted.create(lookupTable)));
    }

    public void forEach(Consumer<? super LookupTableDto> action) {
        try (DBCursor<LookupTableDto> dbCursor = db.find()) {
            dbCursor.forEachRemaining(action);
        }
    }
}
