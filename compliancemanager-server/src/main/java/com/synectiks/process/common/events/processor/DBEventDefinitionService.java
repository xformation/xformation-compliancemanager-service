/*
 * */
package com.synectiks.process.common.events.processor;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.events.notifications.EventNotificationConfig;
import com.synectiks.process.common.security.entities.EntityOwnershipService;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.PaginatedDbService;
import com.synectiks.process.server.database.PaginatedList;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.search.SearchQuery;

import org.mongojack.DBQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public class DBEventDefinitionService extends PaginatedDbService<EventDefinitionDto> {
    private static final Logger LOG = LoggerFactory.getLogger(DBEventDefinitionService.class);

    private static final String COLLECTION_NAME = "event_definitions";

    private final DBEventProcessorStateService stateService;
    private final EntityOwnershipService entityOwnerShipService;

    @Inject
    public DBEventDefinitionService(MongoConnection mongoConnection,
                                    MongoJackObjectMapperProvider mapper,
                                    DBEventProcessorStateService stateService,
                                    EntityOwnershipService entityOwnerShipService) {
        super(mongoConnection, mapper, EventDefinitionDto.class, COLLECTION_NAME);
        this.stateService = stateService;
        this.entityOwnerShipService = entityOwnerShipService;
    }

    public PaginatedList<EventDefinitionDto> searchPaginated(SearchQuery query, Predicate<EventDefinitionDto> filter,
                                                             String sortByField, int page, int perPage) {
        return findPaginatedWithQueryFilterAndSort(query.toDBQuery(), filter,
                getSortBuilder("asc", sortByField), page, perPage);
    }

    public EventDefinitionDto saveWithOwnership(EventDefinitionDto eventDefinitionDto, User user) {
        final EventDefinitionDto dto = super.save(eventDefinitionDto);
        entityOwnerShipService.registerNewEventDefinition(dto.id(), user);
        return dto;
    }

    @Override
    public int delete(String id) {
        try {
            stateService.deleteByEventDefinitionId(id);
        } catch (Exception e) {
            LOG.error("Couldn't delete event processor state for <{}>", id, e);
        }
        entityOwnerShipService.unregisterEventDefinition(id);
        return super.delete(id);
    }

    /**
     * Returns the list of event definitions that is using the given notification ID.
     *
     * @param notificationId the notification ID
     * @return the event definitions with the given notification ID
     */
    public List<EventDefinitionDto> getByNotificationId(String notificationId) {
        final String field = String.format(Locale.US, "%s.%s",
            EventDefinitionDto.FIELD_NOTIFICATIONS,
            EventNotificationConfig.FIELD_NOTIFICATION_ID);
        return ImmutableList.copyOf((db.find(DBQuery.is(field, notificationId)).iterator()));
    }
}
