/*
 * */
package com.synectiks.process.server.rest.resources.streams.alerts;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.alerts.AlertService;
import com.synectiks.process.server.plugin.alarms.AlertCondition;
import com.synectiks.process.server.plugin.configuration.ConfigurableTypeInfo;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.rest.models.streams.alerts.AlertConditionListSummary;
import com.synectiks.process.server.rest.models.streams.alerts.AlertConditionSummary;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.streams.StreamService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static com.synectiks.process.server.shared.security.RestPermissions.STREAMS_READ;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiresAuthentication
@Api(value = "AlertConditions", description = "Manage stream legacy alert conditions")
@Path("/alerts/conditions")
@Produces(MediaType.APPLICATION_JSON)
public class AlertConditionsResource extends RestResource {
    private final Map<String, AlertCondition.Factory> alertConditionTypesMap;
    private final StreamService streamService;
    private final AlertService alertService;

    @Inject
    public AlertConditionsResource(Map<String, AlertCondition.Factory> alertConditionTypesMap,
                                   StreamService streamService,
                                   AlertService alertService) {
        this.alertConditionTypesMap = alertConditionTypesMap;
        this.streamService = streamService;
        this.alertService = alertService;
    }

    @GET
    @Timed
    @ApiOperation(value = "Get a list of all alert conditions")
    public AlertConditionListSummary all() {
        final List<Stream> streams = streamService.loadAll();

        final List<AlertConditionSummary> conditionSummaries = streams.stream()
                .filter(stream -> isPermitted(STREAMS_READ, stream.getId()))
                .flatMap(stream -> streamService.getAlertConditions(stream).stream()
                        .map(condition -> AlertConditionSummary.create(condition.getId(),
                                condition.getType(),
                                condition.getCreatorUserId(),
                                condition.getCreatedAt().toDate(),
                                condition.getParameters(),
                                alertService.inGracePeriod(condition),
                                condition.getTitle()))
                ).collect(Collectors.toList());

        return AlertConditionListSummary.create(conditionSummaries);
    }

    @GET
    @Path("/types")
    @Timed
    @ApiOperation(value = "Get a list of all alert condition types")
    public Map<String, ConfigurableTypeInfo> available() {
        return this.alertConditionTypesMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> ConfigurableTypeInfo.create(entry.getKey(), entry.getValue().descriptor(), entry.getValue().config().getRequestedConfiguration())
                ));
    }
}
