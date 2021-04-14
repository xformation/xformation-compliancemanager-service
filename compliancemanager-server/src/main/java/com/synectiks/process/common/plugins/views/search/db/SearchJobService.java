/*
 * */
package com.synectiks.process.common.plugins.views.search.db;

import java.util.Optional;

import com.synectiks.process.common.plugins.views.search.Search;
import com.synectiks.process.common.plugins.views.search.SearchJob;

public interface SearchJobService {

    SearchJob create(Search query, String owner);
    Optional<SearchJob> load(String id, String owner);
}
