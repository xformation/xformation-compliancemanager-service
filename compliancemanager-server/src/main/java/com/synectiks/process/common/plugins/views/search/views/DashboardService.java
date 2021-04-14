/*
 * */
package com.synectiks.process.common.plugins.views.search.views;

import com.synectiks.process.server.database.PaginatedList;
import com.synectiks.process.server.search.SearchQuery;

import javax.inject.Inject;

public class DashboardService {
    private final ViewService viewService;

    @Inject
    public DashboardService(ViewService viewService) {
        this.viewService = viewService;
    }

    public long count() {
        final PaginatedList<ViewDTO> result = viewService.searchPaginatedByType(ViewDTO.Type.DASHBOARD, new SearchQuery(""), dashboard -> true, "ASC", ViewDTO.FIELD_ID, 1, 0);
        return result.grandTotal().orElseThrow(() -> new IllegalStateException("Missing grand total in response when counting dashboards!"));
    }
}
