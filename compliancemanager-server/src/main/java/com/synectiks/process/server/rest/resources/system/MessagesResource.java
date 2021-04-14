/*
 * */
package com.synectiks.process.server.rest.resources.system;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;
import com.synectiks.process.server.system.activities.SystemMessage;
import com.synectiks.process.server.system.activities.SystemMessageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@RequiresAuthentication
@Api(value = "System/Messages", description = "Internal alertmanager messages")
@Path("/system/messages")
public class MessagesResource extends RestResource {
    private final SystemMessageService systemMessageService;

    @Inject
    public MessagesResource(SystemMessageService systemMessageService) {
        this.systemMessageService = systemMessageService;
    }

    @GET
    @Timed
    @ApiOperation(value = "Get internal alertmanager system messages")
    @RequiresPermissions(RestPermissions.SYSTEMMESSAGES_READ)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> all(@ApiParam(name = "page", value = "Page") @QueryParam("page") int page) {
        final List<Map<String, Object>> messages = Lists.newArrayList();
        for (SystemMessage sm : systemMessageService.all(page(page))) {
            Map<String, Object> message = Maps.newHashMapWithExpectedSize(4);
            message.put("caller", sm.getCaller());
            message.put("content", sm.getContent());
            message.put("timestamp", Tools.getISO8601String(sm.getTimestamp()));
            message.put("node_id", sm.getNodeId());

            messages.add(message);
        }

        return ImmutableMap.of(
                "messages", messages,
                "total", systemMessageService.totalCount());
    }

    private int page(int page) {
        return Math.max(0, page - 1);
    }
}
