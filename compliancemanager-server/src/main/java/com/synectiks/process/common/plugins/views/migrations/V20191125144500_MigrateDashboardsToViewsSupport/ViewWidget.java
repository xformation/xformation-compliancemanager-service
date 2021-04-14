/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport;

import java.util.Set;

public interface ViewWidget {
    String id();
    Set<SearchType> toSearchTypes(RandomUUIDProvider randomUUIDProvider);
}
