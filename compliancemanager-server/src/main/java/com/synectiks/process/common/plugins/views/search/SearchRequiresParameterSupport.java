/*
 * */
package com.synectiks.process.common.plugins.views.search;

import com.synectiks.process.common.plugins.views.Requirement;
import com.synectiks.process.common.plugins.views.search.views.EnterpriseMetadataSummary;
import com.synectiks.process.common.plugins.views.search.views.PluginMetadataSummary;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;

public class SearchRequiresParameterSupport implements Requirement<Search> {
    public static final String Parameters = "parameters";

    private final EnterpriseMetadataSummary enterpriseMetadataSummary;

    @Inject
    public SearchRequiresParameterSupport(EnterpriseMetadataSummary enterpriseMetadataSummary) {
        this.enterpriseMetadataSummary = enterpriseMetadataSummary;
    }

    @Override
    public Map<String, PluginMetadataSummary> test(Search search) {
        return search.parameters().isEmpty()
                ? Collections.emptyMap()
                : Collections.singletonMap(Parameters, enterpriseMetadataSummary);
    }
}
