/*
 * */
package com.synectiks.process.common.plugins.views.search;

import com.synectiks.process.common.plugins.views.search.db.SearchDbService;
import com.synectiks.process.common.plugins.views.search.errors.PermissionException;
import com.synectiks.process.common.plugins.views.search.views.ViewDTO;
import com.synectiks.process.common.plugins.views.search.views.ViewService;
import com.synectiks.process.server.plugin.database.users.User;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SearchDomain {
    private final SearchDbService dbService;
    private final ViewService viewService;

    @Inject
    public SearchDomain(SearchDbService dbService, ViewService viewService) {
        this.dbService = dbService;
        this.viewService = viewService;
    }

    public Optional<Search> getForUser(String id, User user, Predicate<ViewDTO> viewReadPermission) {
        final Optional<Search> search = dbService.get(id);

        search.ifPresent(s -> checkPermission(user, viewReadPermission, s));

        return search;
    }

    private void checkPermission(User user, Predicate<ViewDTO> viewReadPermission, Search s) {
        if (!hasReadPermissionFor(user, viewReadPermission, s))
            throw new PermissionException("User " + user.getName() + " does not have permission to load search " + s.id());
    }

    public List<Search> getAllForUser(User user, Predicate<ViewDTO> viewReadPermission) {
        return dbService.streamAll()
                .filter(s -> hasReadPermissionFor(user, viewReadPermission, s))
                .collect(Collectors.toList());
    }

    private boolean hasReadPermissionFor(User user, Predicate<ViewDTO> viewReadPermission, Search search) {
        if (isOwned(search, user)) {
            return true;
        }
        // Allowed if permissions exist for a referencing view
        final Collection<ViewDTO> views = viewService.forSearch(search.id());
        if (views.isEmpty())
            return false;

        return views.stream().anyMatch(viewReadPermission);
    }

    private boolean isOwned(Search search, User user) {
        return search.owner().map(o -> o.equals(user.getName())).orElse(false);
    }
}
