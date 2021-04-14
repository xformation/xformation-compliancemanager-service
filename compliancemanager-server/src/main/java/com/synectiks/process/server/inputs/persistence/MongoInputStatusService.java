/*
 * */
package com.synectiks.process.server.inputs.persistence;

import com.google.common.eventbus.EventBus;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.inputs.InputService;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputDeleted;

import org.apache.shiro.event.Subscribe;
import org.bson.types.ObjectId;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

/**
 * This is a simple wrapper around MongoDB to allow storage of state data for stateful Inputs.
 *
 * Inputs using this service are responsible for defining their own model for InputStatusRecord.inputStateData
 */
@Singleton
public class MongoInputStatusService implements InputStatusService {
    private static final Logger LOG = LoggerFactory.getLogger(MongoInputStatusService.class);

    public static final String COLLECTION_NAME = "input_status";

    private final JacksonDBCollection<InputStatusRecord, ObjectId> statusCollection;
    private final InputService inputService;

    @Inject
    public MongoInputStatusService(MongoConnection mongoConnection,
                                   MongoJackObjectMapperProvider objectMapperProvider,
                                   InputService inputService,
                                   EventBus eventBus) {
        this.inputService = inputService;
        DB mongoDatabase = mongoConnection.getDatabase();
        DBCollection collection = mongoDatabase.getCollection(COLLECTION_NAME);

        eventBus.register(this);

        statusCollection = JacksonDBCollection.wrap(
                collection,
                InputStatusRecord.class,
                ObjectId.class,
                objectMapperProvider.get());
    }

    @Override
    public Optional<InputStatusRecord> get(final String inputId) {
        return Optional.ofNullable(statusCollection.findOneById(new ObjectId(inputId)));
    }

    @Override
    public InputStatusRecord save(InputStatusRecord statusRecord) {
        final WriteResult<InputStatusRecord, ObjectId> save = statusCollection.save(statusRecord);
        return save.getSavedObject();
    }

    @Override
    public int delete(String inputId) {
        final WriteResult<InputStatusRecord, ObjectId> delete = statusCollection.removeById(new ObjectId(inputId));
        return delete.getN();
    }

    /**
     * Clean up MongoDB records when Inputs are deleted
     *
     * At the moment, alertmanager uses the InputDeleted event both when an Input is stopped
     * and when it is deleted.
     *
     * @param event ID of the input being deleted
     */
    @Subscribe
    public void handleInputDeleted(InputDeleted event) {
        LOG.debug("Input Deleted event received for Input [{}]", event.id());

        // The input system is currently sending an "InputDeleted" event when an input gets deleted AND when an
        // input gets stopped. Check the database if the input was only stopped or actually deleted.
        try {
            inputService.find(event.id());
            // The input still exists so it only has been stopped. Don't do anything.
        } catch (NotFoundException e) {
            // The input is actually gone (deleted) so we can remove the state.
            LOG.debug("Deleting state for input <{}> from database", event.id());
            delete(event.id());
        }
    }
}
