/*
 * */
package com.synectiks.process.common.plugins.sidecar.services;

import com.google.inject.Singleton;
import com.mongodb.BasicDBObject;
import com.synectiks.process.common.plugins.sidecar.rest.models.ConfigurationVariable;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.PaginatedDbService;

import org.mongojack.DBQuery;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Strings.isNullOrEmpty;

@Singleton
public class ConfigurationVariableService extends PaginatedDbService<ConfigurationVariable> {
    private static final String COLLECTION_NAME = "sidecar_configuration_variables";

    @Inject
    public ConfigurationVariableService(MongoConnection mongoConnection,
                                        MongoJackObjectMapperProvider mapper) {
        super(mongoConnection, mapper, ConfigurationVariable.class, COLLECTION_NAME);
        db.createIndex(new BasicDBObject("name", 1), new BasicDBObject("unique", true));
    }

    public List<ConfigurationVariable> all() {
        try (final Stream<ConfigurationVariable> configurationVariableStream =
                     streamQueryWithSort(DBQuery.empty(), getSortBuilder("asc", "name"))) {
            return configurationVariableStream.collect(Collectors.toList());
        }
    }

    public ConfigurationVariable fromRequest(ConfigurationVariable request) {
        return ConfigurationVariable.create(
                request.name(),
                request.description(),
                request.content());
    }

    public ConfigurationVariable fromRequest(String id, ConfigurationVariable request) {
        return ConfigurationVariable.create(
                id,
                request.name(),
                request.description(),
                request.content());
    }

    public ConfigurationVariable find(String id) {
        return db.findOne(DBQuery.is("_id", id));
    }

    public ConfigurationVariable findByName(String name) {
        return db.findOne(DBQuery.is("name", name));
    }

    public boolean hasConflict(ConfigurationVariable variable) {
       final DBQuery.Query query;

       if (isNullOrEmpty(variable.id())) {
           query = DBQuery.is(ConfigurationVariable.FIELD_NAME, variable.name());
       } else {
           // updating an existing variable, don't match against itself
           query = DBQuery.and(
                           DBQuery.is(ConfigurationVariable.FIELD_NAME, variable.name()),
                           DBQuery.notEquals("_id", variable.id()
                           )
           );
       }
       return db.getCount(query) > 0;
    }

    @Override
    public ConfigurationVariable save(ConfigurationVariable configurationVariable) {
        return db.findAndModify(DBQuery.is("_id", configurationVariable.id()), new BasicDBObject(),
                new BasicDBObject(), false, configurationVariable, true, true);
    }
}
