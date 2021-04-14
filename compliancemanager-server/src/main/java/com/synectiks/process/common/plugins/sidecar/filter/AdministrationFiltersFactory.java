/*
 * */
package com.synectiks.process.common.plugins.sidecar.filter;


import javax.inject.Inject;

import com.synectiks.process.common.plugins.sidecar.rest.models.Sidecar;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class AdministrationFiltersFactory {
    private final AdministrationFilter.Factory administrationFilterFactory;

    @Inject
    public AdministrationFiltersFactory(AdministrationFilter.Factory administrationFilterFactory) {
        this.administrationFilterFactory = administrationFilterFactory;
    }

    public Optional<Predicate<Sidecar>> getFilters(Map<String, String> filters) {
        return filters.entrySet().stream()
                .map((Function<Map.Entry<String, String>, Predicate<Sidecar>>) entry -> {
                    final String name = entry.getKey();
                    final String value = entry.getValue();

                    final AdministrationFilter.Type filter = AdministrationFilter.Type.valueOf(name.toUpperCase(Locale.ENGLISH));
                    switch (filter) {
                        case COLLECTOR:
                            return administrationFilterFactory.createCollectorFilter(value);
                        case CONFIGURATION:
                            return administrationFilterFactory.createConfigurationFilter(value);
                        case OS:
                            return administrationFilterFactory.createOsFilter(value);
                        case STATUS:
                            return administrationFilterFactory.createStatusFilter(Integer.valueOf(value));
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                // We join all filters with an and condition, so results will need to match all filters
                .reduce(Predicate::and);
    }
}
