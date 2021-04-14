/*
 * */
package com.synectiks.process.common.security.authservice;

import com.google.common.eventbus.EventBus;
import com.synectiks.process.common.security.events.AuthServiceBackendDeletedEvent;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.PaginatedDbService;
import com.synectiks.process.server.database.PaginatedList;
import com.synectiks.process.server.rest.PaginationParameters;

import org.mongojack.DBQuery;
import org.mongojack.DBSort;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class DBAuthServiceBackendService extends PaginatedDbService<AuthServiceBackendDTO> {
    private final Map<String, AuthServiceBackend.Factory<? extends AuthServiceBackend>> backendFactories;
    private final EventBus eventBus;

    @Inject
    protected DBAuthServiceBackendService(MongoConnection mongoConnection,
                                          MongoJackObjectMapperProvider mapper,
                                          Map<String, AuthServiceBackend.Factory<? extends AuthServiceBackend>> backendFactories,
                                          EventBus eventBus) {
        super(mongoConnection, mapper, AuthServiceBackendDTO.class, "auth_service_backends");
        this.backendFactories = backendFactories;
        this.eventBus = eventBus;
    }

    @Override
    public AuthServiceBackendDTO save(AuthServiceBackendDTO newBackend) {
        return super.save(prepareUpdate(newBackend));
    }

    @Override
    public int delete(String id) {
        checkArgument(isNotBlank(id), "id cannot be blank");
        final int delete = super.delete(id);
        if (delete > 0) {
            eventBus.post(AuthServiceBackendDeletedEvent.create(id));
        }
        return delete;
    }

    private AuthServiceBackendDTO prepareUpdate(AuthServiceBackendDTO newBackend) {
        if (newBackend.id() == null) {
            // It's not an update
            return newBackend;
        }
        final AuthServiceBackendDTO existingBackend = get(newBackend.id())
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find backend <" + newBackend.id() + ">"));

        // Call AuthServiceBackend#prepareConfigUpdate to give the backend implementation a chance to modify it
        // (e.g. handling password updates via EncryptedValue)
        return Optional.ofNullable(backendFactories.get(existingBackend.config().type()))
                .map(factory -> factory.create(existingBackend))
                .map(backend -> backend.prepareConfigUpdate(existingBackend, newBackend))
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find backend implementation for type <" + existingBackend.config().type() + ">"));
    }

    public long countBackends() {
        return db.count();
    }

    public PaginatedList<AuthServiceBackendDTO> findPaginated(PaginationParameters params,
                                                              Predicate<AuthServiceBackendDTO> filter) {
        final String sortBy = defaultIfBlank(params.getSortBy(), "title");
        final DBSort.SortBuilder sortBuilder = getSortBuilder(params.getOrder(), sortBy);

        return findPaginatedWithQueryFilterAndSort(DBQuery.empty(), filter, sortBuilder, params.getPage(), params.getPerPage());
    }
}
