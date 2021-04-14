/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view;

import java.util.Set;

import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.search.SearchType;

public interface ViewWidget {
    String id();
    Set<SearchType> toSearchTypes(RandomUUIDProvider randomUUIDProvider);
}
