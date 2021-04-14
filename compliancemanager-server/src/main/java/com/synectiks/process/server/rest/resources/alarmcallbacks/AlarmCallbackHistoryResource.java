/*
 * */
package com.synectiks.process.server.rest.resources.alarmcallbacks;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackHistory;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackHistoryService;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.rest.models.alarmcallbacks.AlarmCallbackHistoryListSummary;
import com.synectiks.process.server.rest.models.alarmcallbacks.AlarmCallbackHistorySummary;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@RequiresAuthentication
@Api(value = "AlarmCallbackHistories", description = "Manage stream legacy alarm callback histories")
@Path("/streams/{streamid}/alerts/{alertId}/history")
public class AlarmCallbackHistoryResource extends RestResource {
    private final AlarmCallbackHistoryService alarmCallbackHistoryService;

    @Inject
    public AlarmCallbackHistoryResource(AlarmCallbackHistoryService alarmCallbackHistoryService) {
        this.alarmCallbackHistoryService = alarmCallbackHistoryService;
    }

    @GET
    @Timed
    @ApiOperation(value = "Get a list of all alarm callbacks for this stream")
    @Produces(MediaType.APPLICATION_JSON)
    public AlarmCallbackHistoryListSummary getForAlert(@ApiParam(name = "streamid", value = "The id of the stream whose alarm callbacks history we want.", required = true)
                                                       @PathParam("streamid") String streamid,
                                                       @ApiParam(name = "alertId", value = "The id of the alert whose callback history we want.", required = true)
                                                       @PathParam("alertId") String alertId) throws NotFoundException {
        checkPermission(RestPermissions.STREAMS_READ, streamid);

        final List<AlarmCallbackHistory> historyList = this.alarmCallbackHistoryService.getForAlertId(alertId);

        final List<AlarmCallbackHistorySummary> historySummaryList = Lists.newArrayListWithCapacity(historyList.size());
        for (AlarmCallbackHistory alarmCallbackHistory : historyList) {
            historySummaryList.add(AlarmCallbackHistorySummary.create(alarmCallbackHistory.id(),
                    alarmCallbackHistory.alarmcallbackConfiguration(),
                    alarmCallbackHistory.alertId(),
                    alarmCallbackHistory.alertConditionId(),
                    alarmCallbackHistory.result(),
                    alarmCallbackHistory.createdAt()));
        }

        return AlarmCallbackHistoryListSummary.create(historySummaryList);
    }
}
