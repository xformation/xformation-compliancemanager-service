/*
 * */
package com.synectiks.process.common.plugins.views.search;

import com.google.inject.assistedinject.Assisted;
import com.synectiks.process.common.plugins.views.Requirement;
import com.synectiks.process.common.plugins.views.Requirements;

import javax.inject.Inject;
import java.util.Set;

public class SearchRequirements extends Requirements<Search> {
    @Inject
    public SearchRequirements(Set<Requirement<Search>> requirements, @Assisted Search dto) {
        super(requirements, dto);
    }

    public interface Factory extends Requirements.Factory<Search> {
        SearchRequirements create(Search dto);
    }
}
