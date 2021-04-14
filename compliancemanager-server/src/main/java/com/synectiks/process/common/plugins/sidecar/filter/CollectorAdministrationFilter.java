/*
 * */
package com.synectiks.process.common.plugins.sidecar.filter;

import com.google.inject.assistedinject.Assisted;
import com.synectiks.process.common.plugins.sidecar.rest.models.Collector;
import com.synectiks.process.common.plugins.sidecar.rest.models.Sidecar;
import com.synectiks.process.common.plugins.sidecar.services.CollectorService;

import javax.inject.Inject;

public class CollectorAdministrationFilter implements AdministrationFilter {
    private final Collector collector;

    @Inject
    public CollectorAdministrationFilter(CollectorService collectorService,
                                         @Assisted String collectorId) {
        this.collector = collectorService.find(collectorId);
    }

    @Override
    public boolean test(Sidecar sidecar) {
        return collector.nodeOperatingSystem().equalsIgnoreCase(sidecar.nodeDetails().operatingSystem());
    }
}
