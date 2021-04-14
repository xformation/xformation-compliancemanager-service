/*
 * */
package com.synectiks.process.common.plugins.sidecar.filter;

import com.google.inject.assistedinject.Assisted;
import com.synectiks.process.common.plugins.sidecar.rest.models.CollectorStatusList;
import com.synectiks.process.common.plugins.sidecar.rest.models.Sidecar;

import javax.inject.Inject;

public class StatusAdministrationFilter implements AdministrationFilter {
    private final Sidecar.Status status;

    @Inject
    public StatusAdministrationFilter(@Assisted int status) {
        this.status = Sidecar.Status.fromStatusCode(status);
    }

    @Override
    public boolean test(Sidecar sidecar) {
        final CollectorStatusList collectorStatusList = sidecar.nodeDetails().statusList();
        if (collectorStatusList == null) {
            // Sidecars with not known status are in an UNKNOWN status
            return Sidecar.Status.UNKNOWN.equals(status);
        }
        return collectorStatusList.collectors().stream()
                .anyMatch(status -> Sidecar.Status.fromStatusCode(status.status()).equals(this.status));
    }
}
