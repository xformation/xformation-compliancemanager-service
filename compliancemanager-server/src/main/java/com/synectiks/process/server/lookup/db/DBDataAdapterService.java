/*
 * */
package com.synectiks.process.server.lookup.db;

import com.google.common.collect.ImmutableList;
import com.mongodb.BasicDBObject;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.PaginatedList;
import com.synectiks.process.server.events.ClusterEventBus;
import com.synectiks.process.server.lookup.dto.DataAdapterDto;
import com.synectiks.process.server.lookup.events.DataAdaptersDeleted;
import com.synectiks.process.server.lookup.events.DataAdaptersUpdated;

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
import java.util.Set;
import java.util.stream.Collectors;

public class DBDataAdapterService {
    private final JacksonDBCollection<DataAdapterDto, ObjectId> db;
    private final ClusterEventBus clusterEventBus;

    @Inject
    public DBDataAdapterService(MongoConnection mongoConnection,
                                MongoJackObjectMapperProvider mapper,
                                ClusterEventBus clusterEventBus) {
        this.db = JacksonDBCollection.wrap(mongoConnection.getDatabase().getCollection("lut_data_adapters"),
                DataAdapterDto.class,
                ObjectId.class,
                mapper.get());
        this.clusterEventBus = clusterEventBus;

        db.createIndex(new BasicDBObject("name", 1), new BasicDBObject("unique", true));
    }

    public Optional<DataAdapterDto> get(String idOrName) {
        try {
            return Optional.ofNullable(db.findOneById(new ObjectId(idOrName)));
        } catch (IllegalArgumentException e) {
            // not an ObjectId, try again with name
            return Optional.ofNullable(db.findOne(DBQuery.is("name", idOrName)));

        }
    }

    public DataAdapterDto save(DataAdapterDto table) {
        WriteResult<DataAdapterDto, ObjectId> save = db.save(table);
        final DataAdapterDto savedDataAdapter = save.getSavedObject();
        clusterEventBus.post(DataAdaptersUpdated.create(savedDataAdapter.id()));

        return savedDataAdapter;
    }

    public PaginatedList<DataAdapterDto> findPaginated(DBQuery.Query query, DBSort.SortBuilder sort, int page, int perPage) {
        try (DBCursor<DataAdapterDto> cursor = db.find(query)
                .sort(sort)
                .limit(perPage)
                .skip(perPage * Math.max(0, page - 1))) {

            return new PaginatedList<>(asImmutableList(cursor), cursor.count(), page, perPage);
        }
    }

    private ImmutableList<DataAdapterDto> asImmutableList(Iterator<? extends DataAdapterDto> cursor) {
        return ImmutableList.copyOf(cursor);
    }

    public void delete(String idOrName) {
        final Optional<DataAdapterDto> dataAdapterDto = get(idOrName);
        dataAdapterDto
                .map(DataAdapterDto::id)
                .map(ObjectId::new)
                .ifPresent(db::removeById);
        dataAdapterDto.ifPresent(dataAdapter -> clusterEventBus.post(DataAdaptersDeleted.create(dataAdapter.id())));
    }

    public Collection<DataAdapterDto> findByIds(Set<String> idSet) {
        final DBQuery.Query query = DBQuery.in("_id", idSet.stream().map(ObjectId::new).collect(Collectors.toList()));
        try (DBCursor<DataAdapterDto> cursor = db.find(query)) {
            return asImmutableList(cursor);
        }
    }

    public Collection<DataAdapterDto> findAll() {
        try (DBCursor<DataAdapterDto> cursor = db.find()) {
            return asImmutableList(cursor);
        }
    }
}
