/*
 * */
package com.synectiks.process.common.plugins.views.search.timeranges;

import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.SearchType;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

public interface DerivableTimeRange {
    TimeRange deriveTimeRange(Query query, SearchType searchType);
}
